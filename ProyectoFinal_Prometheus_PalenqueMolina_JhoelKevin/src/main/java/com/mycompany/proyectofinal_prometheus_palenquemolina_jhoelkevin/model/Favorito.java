/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model;

/**
 *
 * @author isard
 */
public class Favorito {
    private int id;
    private int usuarioId;
    private int libroId;

    public Favorito() {}

    public Favorito(int id, int usuarioId, int libroId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroId = libroId;
    }

    // Getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getLibroId() {
        return libroId;
    }
    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }
}
