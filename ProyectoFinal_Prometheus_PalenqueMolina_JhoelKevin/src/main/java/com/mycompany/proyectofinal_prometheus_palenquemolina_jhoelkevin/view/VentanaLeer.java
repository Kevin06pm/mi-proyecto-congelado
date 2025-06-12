/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.CapituloController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.ComentarioController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.UsuarioController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Capitulo;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Comentario;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author isard
 */
public class VentanaLeer extends javax.swing.JFrame {

    private Libro libro;
    private Usuario usuarioActivo;

    private ComentarioController comentarioController;
    private DefaultTableModel comentariosModel;
    
    private CapituloController capController;
    private List<Capitulo> listaCapitulos;
    private int capActual;
    private UsuarioController usuarioController;
    private List<Comentario> listaComentarios;
    
    public VentanaLeer(Libro libro, Usuario usuario) {
        initComponents();
        
        // Guardamos referencias
        this.libro          = libro;
        this.usuarioActivo  = usuario;
        this.capController       = new CapituloController();
        this.comentarioController = new ComentarioController();
        this.usuarioController    = new UsuarioController();

        // Configuramos área de lectura
        txtContenido.setEditable(false);
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        jScrollPane1.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        // Inicializamos combo de capítulos
        listaCapitulos = capController.obtenerCapitulos(libro.getId());
        cbCapitulos.removeAllItems();
        for (Capitulo c : listaCapitulos) {
            cbCapitulos.addItem(c.getTitulo());
        }
        capActual = 0;
        if (!listaCapitulos.isEmpty()) {
            cbCapitulos.setSelectedIndex(0);
            cargarCapitulo(0);
        }

        // Listeners de navegación
        btnAnterior.addActionListener(evt -> {
            if (capActual > 0) {
                capActual--;
                cbCapitulos.setSelectedIndex(capActual);
                cargarCapitulo(capActual);
            }
        });
        btnSiguiente.addActionListener(evt -> {
            if (capActual < listaCapitulos.size() - 1) {
                capActual++;
                cbCapitulos.setSelectedIndex(capActual);
                cargarCapitulo(capActual);
            }
        });
        cbCapitulos.addActionListener(evt -> {
            int idx = cbCapitulos.getSelectedIndex();
            if (idx >= 0 && idx < listaCapitulos.size()) {
                capActual = idx;
                cargarCapitulo(capActual);
            }
        });

        // Inicializamos la tabla de comentarios
        comentariosModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Usuario", "Comentario", "Fecha"}
        );
        tblComentarios.setModel(comentariosModel);
        tblComentarios.setRowHeight(24);  // fila más estrecha

        // Centrar texto en las columnas
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblComentarios.getColumnModel().getColumn(0).setCellRenderer(center);
        tblComentarios.getColumnModel().getColumn(1).setCellRenderer(center);
        tblComentarios.getColumnModel().getColumn(2).setCellRenderer(center);

        // Hacer la tabla no editable
        tblComentarios.setDefaultEditor(Object.class, null);

        // Carga inicial de comentarios (puebla listaComentarios)
        cargarComentarios();

        // Listener para enviar nuevo comentario
        btnEnviarComentario.addActionListener(evt -> {
            String texto = txtComentario.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Escribe algo antes de enviar", "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            boolean ok = comentarioController.agregarComentario(
                libro.getId(),
                usuarioActivo.getId(),
                texto
            );
            if (ok) {
                txtComentario.setText("");
                cargarComentarios();  // refresca tabla y listaComentarios
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar comentario", "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Listener para eliminar comentario
        btnEliminarComentario.addActionListener(evt -> {
            int fila = tblComentarios.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona un comentario para eliminar", "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            Comentario seleccionado = listaComentarios.get(fila);
            // Solo puede borrar si es suyo
            if (seleccionado.getUsuarioId() != usuarioActivo.getId()) {
                JOptionPane.showMessageDialog(this,
                    "Solo puedes eliminar tus propios comentarios", "Prohibido",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            // Confirmación
            int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres borrar este comentario?",
                "Confirmar borrado",
                JOptionPane.YES_NO_OPTION
            );
            if (resp != JOptionPane.YES_OPTION) return;

            // Borrado efectivo
            if (comentarioController.eliminarComentario(seleccionado.getId())) {
                cargarComentarios();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el comentario", "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    /**
     * Carga el capítulo número idx en la UI.
     */
    private void cargarCapitulo(int idx) {
        Capitulo cap = listaCapitulos.get(idx);
        lblLeyendo.setText(cap.getTitulo());
        txtContenido.setText(cap.getContenido());
        txtContenido.setCaretPosition(0);
    }
    
    /** Carga todos los comentarios de este libro en la tabla. */
    private void cargarComentarios() {
        comentariosModel.setRowCount(0);
        listaComentarios = comentarioController.obtenerComentarios(libro.getId());
        for (Comentario com : listaComentarios) {
            // Recupera nombre de usuario (en lugar de ID)
            Usuario autor = usuarioController.obtenerPorId(com.getUsuarioId());
            String nombre = (autor != null) 
                ? autor.getNombre() 
                : "Desconocido";
            comentariosModel.addRow(new Object[]{
                nombre,
                com.getTexto(),
                com.getFecha()
            });
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblLeyendo = new javax.swing.JLabel();
        scrollPane1 = new java.awt.ScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContenido = new javax.swing.JTextArea();
        btnEnviarComentario = new javax.swing.JButton();
        jScrollPaneComentarios = new javax.swing.JScrollPane();
        tblComentarios = new javax.swing.JTable();
        txtComentario = new javax.swing.JTextField();
        btnVolverBiblioteca = new javax.swing.JButton();
        cbCapitulos = new javax.swing.JComboBox<>();
        btnSiguiente = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btndetalles = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnEliminarComentario = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 255, 255), 4, true));

        lblLeyendo.setBackground(new java.awt.Color(204, 255, 204));
        lblLeyendo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLeyendo.setForeground(new java.awt.Color(255, 255, 255));
        lblLeyendo.setText("Titulo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(lblLeyendo, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(lblLeyendo)
                .addGap(14, 14, 14))
        );

        txtContenido.setBackground(new java.awt.Color(51, 51, 51));
        txtContenido.setColumns(20);
        txtContenido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtContenido.setForeground(new java.awt.Color(255, 255, 255));
        txtContenido.setRows(5);
        jScrollPane1.setViewportView(txtContenido);

        scrollPane1.add(jScrollPane1);

        btnEnviarComentario.setBackground(new java.awt.Color(102, 255, 255));
        btnEnviarComentario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEnviarComentario.setText("Enviar Comentario");

        jScrollPaneComentarios.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPaneComentarios.setForeground(new java.awt.Color(255, 255, 0));

        tblComentarios.setBackground(new java.awt.Color(51, 51, 51));
        tblComentarios.setForeground(new java.awt.Color(255, 255, 0));
        tblComentarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPaneComentarios.setViewportView(tblComentarios);

        btnVolverBiblioteca.setBackground(new java.awt.Color(255, 255, 0));
        btnVolverBiblioteca.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVolverBiblioteca.setText("Volver");
        btnVolverBiblioteca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverBibliotecaActionPerformed(evt);
            }
        });

        cbCapitulos.setBackground(new java.awt.Color(102, 255, 255));
        cbCapitulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbCapitulos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lista de Capitulos" }));
        cbCapitulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCapitulosActionPerformed(evt);
            }
        });

        btnSiguiente.setBackground(new java.awt.Color(102, 255, 255));
        btnSiguiente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSiguiente.setText("Siguiente");

        btnAnterior.setBackground(new java.awt.Color(102, 255, 255));
        btnAnterior.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAnterior.setText("Anterior");

        btndetalles.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btndetalles.setText("Detalles");
        btndetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndetallesActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(102, 255, 255));
        jSeparator1.setForeground(new java.awt.Color(102, 255, 255));
        jSeparator1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator1.setMinimumSize(new java.awt.Dimension(50, 20));

        btnEliminarComentario.setBackground(new java.awt.Color(204, 0, 0));
        btnEliminarComentario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminarComentario.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarComentario.setText("Eliminar comentario");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(btnAnterior)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btndetalles, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(58, 58, 58)
                            .addComponent(btnSiguiente))
                        .addComponent(scrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnVolverBiblioteca)
                            .addComponent(cbCapitulos, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEnviarComentario)))
                        .addGap(0, 40, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEliminarComentario)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnVolverBiblioteca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbCapitulos)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnviarComentario))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPaneComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btndetalles)
                    .addComponent(btnAnterior)
                    .addComponent(btnSiguiente)
                    .addComponent(btnEliminarComentario))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverBibliotecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverBibliotecaActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnVolverBibliotecaActionPerformed

    private void btndetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndetallesActionPerformed
        new VentanaLibroDetalles(this.libro, this.usuarioActivo)
            .setVisible(true);
    }//GEN-LAST:event_btndetallesActionPerformed

    private void cbCapitulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCapitulosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbCapitulosActionPerformed

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
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnEliminarComentario;
    private javax.swing.JButton btnEnviarComentario;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnVolverBiblioteca;
    private javax.swing.JButton btndetalles;
    private javax.swing.JComboBox<String> cbCapitulos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneComentarios;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblLeyendo;
    private java.awt.ScrollPane scrollPane1;
    private javax.swing.JTable tblComentarios;
    private javax.swing.JTextField txtComentario;
    private javax.swing.JTextArea txtContenido;
    // End of variables declaration//GEN-END:variables
}
