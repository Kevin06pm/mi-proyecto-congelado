/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.CapituloController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.FavoritoController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller.PuntuacionController;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Capitulo;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicPanelUI;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author isard
 */
public class VentanaLibroDetalles extends javax.swing.JFrame {

    private final Libro libro;
    private final Usuario usuarioActivo;
    private final FavoritoController favoritoController;
    private final PuntuacionController puntuacionController;
    private boolean esFavoritoActual;
    private final CapituloController capituloController;

    public VentanaLibroDetalles(Libro libro, Usuario usuario) {
        
        initComponents();
        setLocationRelativeTo(null);
        this.capituloController = new CapituloController();
        
        txtSinopsis.setEditable(false);
        // override del modelo para Strings "1".."5"
        cbPuntuacion.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"1","2","3","4","5"}
        ));
        cbPuntuacion.setSelectedIndex(-1);
        
        
        //Inicializamos referencias y controladores ANTES de cargar datos
        this.libro = libro;
        this.usuarioActivo = usuario;
        this.favoritoController = new FavoritoController();
        this.puntuacionController = new PuntuacionController();

        // Ajustes de sinopsis (sin scroll horizontal)
        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        jScrollPane1.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        
        //Listener de puntuación
        btnEnviarPuntuacion.addActionListener(evt -> {
            String sel = (String) cbPuntuacion.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona una puntuación (1–5)",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            int p;
            try {
                p = Integer.parseInt(sel);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Valor de puntuación inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            boolean ok = puntuacionController.puntuar(
                usuarioActivo.getId(),
                libro.getId(),
                p
            );
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "¡Puntuación guardada!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
                );
                cargarDetalles();  // refresca la media
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar la puntuación",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        
        
         // INYECTAR FONDO CON LA PORTADA
        try {
            // 1) Obtenemos la ruta de fichero desde el modelo:
            String path = libro.getPortadaURL();  
            // 2) La convertimos a URL válida:
            File file = new File(path);
            java.net.URL url = file.toURI().toURL();
            // 3) Leemos la imagen
            Image cover = ImageIO.read(url);
            // 4) Establecemos el UI para pintar ese cover de fondo
            jPanel2.setUI(new BasicPanelUI() {
                @Override
                public void paint(Graphics g, javax.swing.JComponent c) {
                    g.drawImage(cover, 0, 0, c.getWidth(), c.getHeight(), c);
                    super.paint(g, c);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Si falla, conserva el fondo sólido original
        }
    
        // Listener para descargar todo el libro en pdf
        btnDescargar.addActionListener(evt -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar libro como PDF");
            chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
            // Nombre por defecto con título limpio
            chooser.setSelectedFile(new File(
                libro.getTitulo().replaceAll("\\W+", "_") + ".pdf"
            ));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File destino = chooser.getSelectedFile();
                if (!destino.getName().toLowerCase().endsWith(".pdf")) {
                    destino = new File(destino.getParentFile(),
                                       destino.getName() + ".pdf");
                }
                try {
                    exportarLibroAPDF(destino);
                    JOptionPane.showMessageDialog(this,
                        "PDF guardado en:\n" + destino.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error al generar PDF:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        });
        
        // Cargamos los detalles
        cargarDetalles();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    
    /**
     * Carga títulos, portadas, estado de favorito y media de puntuación.
     */
    private void cargarDetalles() {
        // Portada
       
        // Título y autor
        lblTitulo.setText("Título: " + libro.getTitulo());
        lblAutor.setText("Autor: "  + libro.getAutor());
        lblgenero.setText("Género: "  + libro.getContenido());


        // Sinopsis
        txtSinopsis.setText(libro.getSinopsis());
        txtSinopsis.setCaretPosition(0);

        // Favoritos
        esFavoritoActual = favoritoController.esFavorito(
            usuarioActivo.getId(), libro.getId()
        );
        btnToggleFavorito.setText(
            esFavoritoActual ? "Quitar de Favoritos" : "Marcar Favorito"
        );

        // Puntuación media
        double media = puntuacionController.mediaLibro(libro.getId());
        lblMedia.setText(String.format("Valoración media: %.1f / 5", media));

        // seleccion por defecto del combo (vacío)
        cbPuntuacion.setSelectedIndex(-1);
    }


    private void actualizarTextoBoton() {
        if (esFavoritoActual) {
            btnToggleFavorito.setText("Quitar de Favoritos");
        } else {
            btnToggleFavorito.setText("Marcar Favorito");
        }
    }
    
    /**
     * Genera un pdf que incluye todos los capítulos del libro
     * y lo guarda en el fichero indicado.
     */
    private void exportarLibroAPDF(File destino) throws IOException {
        List<Capitulo> capitulos = capituloController.obtenerCapitulos(libro.getId());

        try (PDDocument doc = new PDDocument()) {
            // Cargar fuente (asegúrate de que esté en /src/main/resources/fonts/)
            try (InputStream fontIs = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
                if (fontIs == null) {
                    throw new IOException("Fuente DejaVuSans.ttf no encontrada en /fonts/");
                }
                PDType0Font font = PDType0Font.load(doc, fontIs);

                for (Capitulo cap : capitulos) {
                    PDPage page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);

                    try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                        float margin = 50;
                        float yStart = page.getMediaBox().getHeight() - margin;
                        float yOffset = yStart - 25; // Posición inicial del texto

                        // —— TÍTULO DEL CAPITULO
                        cs.beginText();
                        cs.setFont(font, 16);
                        cs.newLineAtOffset(margin, yStart);
                        cs.showText(cap.getTitulo());
                        cs.endText();

                        // —— TEXTO DEL CAPITULO
                        cs.beginText();
                        cs.setFont(font, 12);
                        cs.newLineAtOffset(margin, yOffset);

                        //saltos de línea y dividir por párrafos
                        String texto = cap.getContenido().replaceAll("\\r\\n?", "\n");
                        String[] parrafos = texto.split("\n");

                        for (String parrafo : parrafos) {
                            // Dividir cada párrafo en líneas de 80 prrafos
                            String[] lineas = splitTextoEnLineas(parrafo, 80);

                            for (String linea : lineas) {
                                if (yOffset <= margin) {
                                    //no espacio? pues añade una nueva página
                                    cs.endText();
                                    page = new PDPage(PDRectangle.LETTER);
                                    doc.addPage(page);
                                    cs.beginText();
                                    cs.setFont(font, 12);
                                    yOffset = yStart - 25;
                                    cs.newLineAtOffset(margin, yOffset);
                                }
                                cs.showText(linea);
                                yOffset -= 14; // Espacio entre líneas
                                cs.newLineAtOffset(0, -14);
                            }
                        }
                        cs.endText();
                    }
                }
            }
            doc.save(destino);
        }
    }

    /**
     * Divide un texto en líneas de máximo `maxCaracteres` sin cortar palabras.
     */
    private String[] splitTextoEnLineas(String texto, int maxCaracteres) {
        List<String> lineas = new ArrayList<>();
        if (texto.isEmpty()) {
            return new String[]{""};
        }

        String[] palabras = texto.split(" ");
        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {
            if (lineaActual.length() + palabra.length() + 1 > maxCaracteres) {
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            } else {
                if (lineaActual.length() > 0) {
                    lineaActual.append(" ");
                }
                lineaActual.append(palabra);
            }
        }
        lineas.add(lineaActual.toString());
        return lineas.toArray(new String[0]);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLeer = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSinopsis = new javax.swing.JTextArea();
        btnVolver = new javax.swing.JButton();
        btnToggleFavorito = new javax.swing.JButton();
        lblAutor = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cbPuntuacion = new javax.swing.JComboBox<>();
        lblMedia = new javax.swing.JLabel();
        btnEnviarPuntuacion = new javax.swing.JButton();
        lblgenero = new javax.swing.JLabel();
        btnLeer1 = new javax.swing.JButton();
        btnDescargar = new javax.swing.JButton();

        btnLeer.setBackground(new java.awt.Color(102, 255, 255));
        btnLeer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLeer.setText("Leer");
        btnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeerActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        txtSinopsis.setBackground(new java.awt.Color(51, 51, 51));
        txtSinopsis.setColumns(20);
        txtSinopsis.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSinopsis.setForeground(new java.awt.Color(255, 255, 255));
        txtSinopsis.setRows(5);
        jScrollPane1.setViewportView(txtSinopsis);

        btnVolver.setBackground(new java.awt.Color(255, 255, 0));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        btnToggleFavorito.setBackground(new java.awt.Color(204, 0, 0));
        btnToggleFavorito.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnToggleFavorito.setForeground(new java.awt.Color(255, 255, 255));
        btnToggleFavorito.setText("Quitar Favorito");
        btnToggleFavorito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleFavoritoActionPerformed(evt);
            }
        });

        lblAutor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAutor.setForeground(new java.awt.Color(255, 255, 255));
        lblAutor.setText("Autor");

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Titulo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 353, Short.MAX_VALUE)
        );

        cbPuntuacion.setBackground(new java.awt.Color(204, 0, 0));
        cbPuntuacion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbPuntuacion.setForeground(new java.awt.Color(255, 255, 255));
        cbPuntuacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5" }));

        lblMedia.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMedia.setForeground(new java.awt.Color(255, 255, 0));
        lblMedia.setText("Valoración media: 0.0 / 5");

        btnEnviarPuntuacion.setBackground(new java.awt.Color(102, 255, 255));
        btnEnviarPuntuacion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEnviarPuntuacion.setText("Enviar puntuación");

        lblgenero.setBackground(new java.awt.Color(255, 255, 255));
        lblgenero.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblgenero.setForeground(new java.awt.Color(255, 255, 255));
        lblgenero.setText("Genero");

        btnLeer1.setBackground(new java.awt.Color(102, 255, 255));
        btnLeer1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLeer1.setText("Leer");
        btnLeer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeer1ActionPerformed(evt);
            }
        });

        btnDescargar.setBackground(new java.awt.Color(0, 153, 0));
        btnDescargar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDescargar.setForeground(new java.awt.Color(255, 255, 255));
        btnDescargar.setText("Descargar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVolver)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cbPuntuacion, 0, 103, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEnviarPuntuacion, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblMedia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnToggleFavorito)
                            .addComponent(lblAutor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblgenero, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnLeer1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnDescargar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 55, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnToggleFavorito)
                        .addGap(18, 18, 18)
                        .addComponent(lblTitulo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAutor)
                        .addGap(12, 12, 12)
                        .addComponent(lblgenero)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMedia)
                    .addComponent(btnLeer1)
                    .addComponent(btnDescargar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVolver)
                        .addGap(27, 27, 27))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEnviarPuntuacion)
                            .addComponent(cbPuntuacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(36, Short.MAX_VALUE))))
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

    private void btnToggleFavoritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleFavoritoActionPerformed
        if (esFavoritoActual) {
            boolean ok = favoritoController.eliminarFavorito(usuarioActivo.getId(), libro.getId());
            if (ok) {
                esFavoritoActual = false;
                actualizarTextoBoton();
                JOptionPane.showMessageDialog(this, "Libro eliminado de favoritos");
            }
        } else {
            boolean ok = favoritoController.agregarFavorito(usuarioActivo.getId(), libro.getId());
            if (ok) {
                esFavoritoActual = true;
                actualizarTextoBoton();
                JOptionPane.showMessageDialog(this, "Libro añadido a favoritos");
            }
        }
    }//GEN-LAST:event_btnToggleFavoritoActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        this.dispose();

    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerActionPerformed
        

    }//GEN-LAST:event_btnLeerActionPerformed

    private void btnLeer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeer1ActionPerformed
        VentanaLeer lector = new VentanaLeer(this.libro, this.usuarioActivo);
        lector.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_btnLeer1ActionPerformed

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
            java.util.logging.Logger.getLogger(VentanaLibroDetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaLibroDetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaLibroDetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaLibroDetalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDescargar;
    private javax.swing.JButton btnEnviarPuntuacion;
    private javax.swing.JButton btnLeer;
    private javax.swing.JButton btnLeer1;
    private javax.swing.JButton btnToggleFavorito;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbPuntuacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAutor;
    private javax.swing.JLabel lblMedia;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblgenero;
    private javax.swing.JTextArea txtSinopsis;
    // End of variables declaration//GEN-END:variables
}
