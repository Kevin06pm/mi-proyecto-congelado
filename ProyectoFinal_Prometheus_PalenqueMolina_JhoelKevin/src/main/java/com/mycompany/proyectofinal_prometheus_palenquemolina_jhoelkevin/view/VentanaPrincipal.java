/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.LibroController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author isard
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private Usuario usuarioActivo;
    private LibroController libroController;
    private DefaultTableModel tablaModel;
    private List<Libro> listaLibros;          // libros cargados de la BD

    // —— Campos nuevos para autocompletar ——
    private List<String> allTitles;           // todos los títulos para sugerencias
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;

    private List<Libro> currentLibros;    // lo que s eve ahkira en la tabla
    
    /**
     * Constructor: recibe el usuario logueado.
     */
    public VentanaPrincipal(Usuario usuario) {
        initComponents();            // Autogenerado por NetBeans
        
        
        // fondo del logo
        try {
            Image bg = ImageIO.read(getClass().getResource("/logos.png"));
            jPanel3.setUI(new BasicPanelUI() {
                @Override public void paint(Graphics g, JComponent c) {
                    g.drawImage(bg, 0, 0, c.getWidth(), c.getHeight(), c);
                    super.paint(g, c);
                }
            });
        } catch (Exception e) {  }

        // Fondo del perro
        try {
            Image jack = ImageIO.read(getClass().getResource("/jack.png"));
            jPanel4.setUI(new BasicPanelUI() {
                @Override public void paint(Graphics g, JComponent c) {
                    g.drawImage(jack, 0, 0, c.getWidth(), c.getHeight(), c);
                    super.paint(g, c);
                }
            });
        } catch (Exception e) {  }

        // modelo de tabla con 3 columnas
        tablaModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Portada","Título","Autor"}
        ){
            Class<?>[] types = {ImageIcon.class, String.class, String.class};
            @Override public Class<?> getColumnClass(int col){ return types[col]; }
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tblLibros.setModel(tablaModel);


        // Ajustar altura de fila
        tblLibros.setRowHeight(120);

        // Centrar texto en columnas Título y Autor
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblLibros.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblLibros.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Inicializar controlador y usuario
        this.usuarioActivo   = usuario;
        this.libroController = new LibroController();

        // Cargar datos e índice
        cargarTablaLibros();
        extraerTodosLosTitulos();

        // Mostrar saludo
        lblBienvenido.setText("Bienvenido, " + usuarioActivo.getNombre());

        // Configurar autocompletado
        setupAutoComplete();

        // Filtrar tabla al buscar
        btnBuscar.addActionListener(evt -> {
            String texto = txtBuscar.getText().trim().toLowerCase();
            tablaModel.setRowCount(0);
            List<Libro> filtrados = new ArrayList<>();
            if (texto.isEmpty()) filtrados.addAll(listaLibros);
            else {
                for (Libro l : listaLibros) {
                    if (l.getTitulo().toLowerCase().contains(texto)) {
                        filtrados.add(l);
                    }
                }
            }
            currentLibros = filtrados;
            for (Libro l : filtrados) {
                ImageIcon icono = crearIconoPortada(l.getPortadaURL());
                tablaModel.addRow(new Object[]{icono, l.getTitulo(), l.getAutor()});
            }
        });
        
        // Centrar
        tblLibros.setDefaultEditor(Object.class, null);
        tblLibros.setRowHeight(120);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblLibros.getColumnModel().getColumn(1).setCellRenderer(center);
        tblLibros.getColumnModel().getColumn(2).setCellRenderer(center);
    
    }
    
    /** Lee y escala la portada desde URL o recurso local */
    private ImageIcon crearIconoPortada(String ruta) {
        try {
            URL url;
            // si empieza por http o file:
            if (ruta.startsWith("http://") || ruta.startsWith("https://") || ruta.startsWith("file:")) {
                url = new URL(ruta);
            } else {
                // tratamos como recurso 
                URL r = getClass().getResource("/images/" + ruta);
                if (r != null) {
                    url = r;
                } else {
                    // falback a ruta de fichero
                    url = new File(ruta).toURI().toURL();
                }
            }
            Image img = ImageIO.read(url)
                              .getScaledInstance(80, 120, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            // retorno icono por defecto o null
            URL def = getClass().getResource("/images/defaultCover.png");
            if (def != null) return new ImageIcon(def);
            return null;
        }
    }

    /**
     * Carga todos los libros de la base de datos en la tabla
     */
    private void cargarTablaLibros() {
        listaLibros   = libroController.obtenerTodos();
        currentLibros = new ArrayList<>(listaLibros);
        tablaModel.setRowCount(0);
        for(Libro l:listaLibros){
            ImageIcon icono=null;
            try{
                String path = l.getPortadaURL();
                URL url;
                if(path.startsWith("http"))
                    url = new URL(path);
                else
                    url = new File(path).toURI().toURL();
                Image img = ImageIO.read(url)
                                  .getScaledInstance(80,120,Image.SCALE_SMOOTH);
                icono = new ImageIcon(img);
            }catch(Exception ex){}
            tablaModel.addRow(new Object[]{icono,l.getTitulo(),l.getAutor()});
        }
    }

    /**
     * Llena la lista de todos los títulos para el autocompletado
     */
    private void extraerTodosLosTitulos() {
        allTitles = new ArrayList<>();
        for (Libro l : listaLibros) {
            allTitles.add(l.getTitulo());
        }
    }

    /**
     * Configura el autocompletado con JPopupMenu + JList
     */
    private void setupAutoComplete() {
        suggestionPopup = new JPopupMenu();
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionPopup.add(new JScrollPane(suggestionList));

        // Cuando el texto cambia mostramos sugerencias
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }
        });

        // Al hacer clic en una sugerencia la ponemos en el campo
        suggestionList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        String selected = suggestionList.getSelectedValue();
        txtBuscar.setText(selected);
        suggestionPopup.setVisible(false);
    }
        });
    }

    /**
     * Filtra alltitles por el texto actual y muestra el popup con hasta 5 sugerencias.
     */
    private void showSuggestions() {
        String text = txtBuscar.getText().trim().toLowerCase();
        suggestionModel.clear();
        if (text.isEmpty()) {
            suggestionPopup.setVisible(false);
            return;
        }
        for (String title : allTitles) {
            if (title.toLowerCase().startsWith(text)) {
                suggestionModel.addElement(title);
                if (suggestionModel.size() >= 5) break;
            }
        }
        if (suggestionModel.isEmpty()) {
            suggestionPopup.setVisible(false);
        } else {
            suggestionList.setVisibleRowCount(suggestionModel.size());
            suggestionPopup.show(txtBuscar, 0, txtBuscar.getHeight());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnVerDetalles = new javax.swing.JButton();
        btnLeer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLibros = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        lblBienvenido = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnCerrarSesion = new javax.swing.JButton();
        btnFavoritos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        btnVerDetalles.setBackground(new java.awt.Color(102, 255, 255));
        btnVerDetalles.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVerDetalles.setText("Ver detalles");
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });

        btnLeer.setBackground(new java.awt.Color(102, 255, 255));
        btnLeer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLeer.setText("Leer");
        btnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeerActionPerformed(evt);
            }
        });

        tblLibros.setBackground(new java.awt.Color(51, 51, 51));
        tblLibros.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblLibros.setForeground(new java.awt.Color(255, 255, 0));
        tblLibros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblLibros);

        jPanel2.setBackground(new java.awt.Color(204, 0, 0));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 0), 4, true));

        btnBuscar.setBackground(new java.awt.Color(255, 255, 0));
        btnBuscar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBuscar.setText("Buscar");

        lblBienvenido.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBienvenido.setForeground(new java.awt.Color(255, 255, 255));
        lblBienvenido.setText("jLabel1");

        jPanel3.setBackground(new java.awt.Color(204, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnCerrarSesion.setBackground(new java.awt.Color(255, 255, 0));
        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCerrarSesion.setText("Cerrar Sesión");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(btnCerrarSesion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblBienvenido, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscar)
                .addGap(27, 27, 27))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBienvenido)
                    .addComponent(btnCerrarSesion))
                .addGap(30, 30, 30))
        );

        btnFavoritos.setBackground(new java.awt.Color(102, 255, 255));
        btnFavoritos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFavoritos.setText("Favoritos");
        btnFavoritos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavoritosActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 618, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnVerDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnFavoritos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnLeer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(74, 74, 74))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnFavoritos)
                        .addGap(18, 18, 18)
                        .addComponent(btnLeer)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerDetalles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        int fila = tblLibros.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un libro", "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        Libro libro = currentLibros.get(fila);
        new VentanaLeer(libro, usuarioActivo).setVisible(true);
        
    }//GEN-LAST:event_btnLeerActionPerformed

    private void btnFavoritosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavoritosActionPerformed
        VentanaFavoritos favWin = new VentanaFavoritos(usuarioActivo);
            favWin.setVisible(true);
    }//GEN-LAST:event_btnFavoritosActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        VentanaLogin loginWin = new VentanaLogin();
        loginWin.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed
        int fila = tblLibros.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un libro", "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        Libro libro = currentLibros.get(fila);
        new VentanaLibroDetalles(libro, usuarioActivo).setVisible(true);
        
    }//GEN-LAST:event_btnVerDetallesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnFavoritos;
    private javax.swing.JButton btnLeer;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBienvenido;
    private javax.swing.JTable tblLibros;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
