/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.FavoritoDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import java.util.List;

/**
 *
 * @author isard
 */
public class FavoritoController {
    private final FavoritoDAO favoritoDAO;

    public FavoritoController() {
        this.favoritoDAO = new FavoritoDAO();
    }

    /**
     * Agrega un libro a la lista de favoritos de un usuario.
     * @return true si la operación fue exitosa, false si ocurrió un error
     */
    public boolean agregarFavorito(int usuarioId, int libroId) {
        return favoritoDAO.agregarFavorito(usuarioId, libroId);
    }

    /**
     * Elimina un libro de la lista de favoritos de un usuario.
     * @return true si la operación fue exitosa, false si ocurrió un error
     */
    public boolean eliminarFavorito(int usuarioId, int libroId) {
        return favoritoDAO.eliminarFavorito(usuarioId, libroId);
    }

    /**
     * Obtiene la lista de libros marcados como favoritos por un usuario.
     * @return List<Libro> con los libros favoritos
     */
    public List<Libro> obtenerFavoritos(int usuarioId) {
        return favoritoDAO.getFavoritosByUsuario(usuarioId);
    }

    /**
     * Verifica si un libro ya está marcado como favorito por un usuario.
     * @return true si ya es favorito, false en caso contrario
     */
    public boolean esFavorito(int usuarioId, int libroId) {
        return favoritoDAO.esFavorito(usuarioId, libroId);
    }
}
