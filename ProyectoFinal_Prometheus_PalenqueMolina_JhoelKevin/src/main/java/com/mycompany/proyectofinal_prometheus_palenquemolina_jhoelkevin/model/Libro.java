/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model;

/**
 *
 * @author isard
 */
public class Libro {
     private int id;
    private String titulo;
    private String autor;
    private String portadaURL;
    private String sinopsis;
    private String contenido; // texto simulado

    public Libro() {}

    public Libro(int id, String titulo, String autor, String portadaURL, String sinopsis, String contenido) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.portadaURL = portadaURL;
        this.sinopsis = sinopsis;
        this.contenido = contenido;
    }

    // Getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getPortadaURL() {
        return portadaURL;
    }
    public void setPortadaURL(String portadaURL) {
        this.portadaURL = portadaURL;
    }

    public String getSinopsis() {
        return sinopsis;
    }
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
