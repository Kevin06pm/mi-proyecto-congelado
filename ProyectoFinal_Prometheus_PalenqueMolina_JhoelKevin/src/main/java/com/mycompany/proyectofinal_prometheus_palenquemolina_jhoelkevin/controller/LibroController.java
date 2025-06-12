/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.LibroDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.service.OpenLibraryService;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author isard
 */
public class LibroController {
    private final LibroDAO libroDAO;

    public LibroController() {
        this.libroDAO = new LibroDAO();
    }

    /**
     * Devuelve la lista completa de libros en la base de datos.
     */
    public List<Libro> obtenerTodos() {
        return libroDAO.getAllLibros();
    }

    /**
     * Devuelve un objeto Libro buscado por su ID.
     * @param id identificador del libro
     * @return Libro si existe, o null si no lo encuentra
     */
    public Libro obtenerPorId(int id) {
        return libroDAO.getLibroById(id);
    }

    /**
     * Busca el ID de un libro a partir de su título. Recorrerá la lista completa y comparará títulos.
     * @param titulo el título a buscar
     * @return el ID del libro si lo encuentra, o -1 si no existe
     */
    public int obtenerIdPorTitulo(String titulo) {
        List<Libro> lista = libroDAO.getAllLibros();
        for (Libro l : lista) {
            if (l.getTitulo().equals(titulo)) {
                return l.getId();
            }
        }
        return -1;
    }
    
    // busac en lAPI 
    public List<Libro> buscarEnAPI(String titulo) {
        try {
            return new OpenLibraryService().buscarLibros(titulo);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of(); // en caso de error, devolvemos lista vacía
        }
    }
}
