/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.ComentarioDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Comentario;
import java.util.List;

/**
 *
 * @author isard
 */
public class ComentarioController {
    private final ComentarioDAO dao = new ComentarioDAO();

    public List<Comentario> obtenerComentarios(int libroId) {
        return dao.obtenerPorLibro(libroId);
    }

    public boolean agregarComentario(int libroId, int usuarioId, String texto) {
        Comentario c = new Comentario();
        c.setLibroId(libroId);
        c.setUsuarioId(usuarioId);
        c.setTexto(texto);
        return dao.insertar(c);
    }
     public boolean eliminarComentario(int idComentario) {
        return dao.eliminarComentario(idComentario);
    }
}
