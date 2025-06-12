/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Comentario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author isard
 */
public class ComentarioDAO {
    
    // Obtener lista de comentarios
    public List<Comentario> obtenerPorLibro(int libroId) {
        List<Comentario> lista = new ArrayList<>();
        String sql = "SELECT * FROM comentarios WHERE libro_id = ? ORDER BY fecha DESC";
        try (Connection c = ConexionMySQL.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Comentario com = new Comentario();
                com.setId(rs.getInt("id"));
                com.setLibroId(rs.getInt("libro_id"));
                com.setUsuarioId(rs.getInt("usuario_id"));
                com.setTexto(rs.getString("texto"));
                com.setFecha(rs.getTimestamp("fecha"));
                lista.add(com);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    //insertar comenatario
    public boolean insertar(Comentario com) {
        String sql = "INSERT INTO comentarios(libro_id, usuario_id, texto) VALUES (?,?,?)";
        try (Connection c = ConexionMySQL.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, com.getLibroId());
            ps.setInt(2, com.getUsuarioId());
            ps.setString(3, com.getTexto());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Elimina un comentario por su id
    public boolean eliminarComentario(int idComentario) {
        String sql = "DELETE FROM comentarios WHERE id = ?";
        try (Connection c = ConexionMySQL.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idComentario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
