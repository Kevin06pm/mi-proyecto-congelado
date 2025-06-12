/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.PuntuacionDAO;

/**
 *
 * @author isard
 */
public class PuntuacionController {
    private final PuntuacionDAO dao = new PuntuacionDAO();

    //Guarda o actualiza la puntuación y devuelve true si tuvo exito 
    public boolean puntuar(int usuarioId, int libroId, int puntuacion) {
        return dao.upsert(usuarioId, libroId, puntuacion);
    }

    //Recupera la media de puntuación para un libro 
    public double mediaLibro(int libroId) {
        return dao.obtenerMedia(libroId);
    }
}
