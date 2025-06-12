/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model;

/**
 *
 * @author isard
 */
public class Capitulo {
    private int id;
    private int libroId;
    private int numero;
    private String titulo;
    private String contenido;

    /** Constructor vacío para frameworks y serialización. */
    public Capitulo() { }

    /**
     * Constryuctor de los campos
     *
     * @param id         Identificador único del capítulo
     * @param libroId    ID del libro al que pertenece
     * @param numero     Número de orden del capítulo
     * @param titulo     Título del capítulo
     * @param contenido  Texto completo del capítulo
     */
    public Capitulo(int id, int libroId, int numero, String titulo, String contenido) {
        this.id = id;
        this.libroId = libroId;
        this.numero = numero;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLibroId() {
        return libroId;
    }

    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Capitulo{" +
               "id=" + id +
               ", libroId=" + libroId +
               ", numero=" + numero +
               ", titulo='" + titulo + '\'' +
               '}';
    }
}
