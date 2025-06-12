/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view.VentanaLogin;
import javax.swing.SwingUtilities;

/**
 *
 * @author isard
 */
public class AppMain {
    public static void main(String[] args) {
        // Ejecutar la GUI en el hilo de eventos
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaLogin login = new VentanaLogin();
                login.setVisible(true);
            }
        });
    }
}
