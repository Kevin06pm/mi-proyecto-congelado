/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model;

import java.sql.Timestamp;

/**
 *
 * @author isard
 */
public class Puntuacion {
    private int id;
    private int usuarioId;
    private int libroId;
    private int puntuacion;
    private Timestamp fecha;

    public Puntuacion() {}

    //getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int u) { this.usuarioId = u; }

    public int getLibroId() { return libroId; }
    public void setLibroId(int l) { this.libroId = l; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int p) { this.puntuacion = p; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp f) { this.fecha = f; }
}
