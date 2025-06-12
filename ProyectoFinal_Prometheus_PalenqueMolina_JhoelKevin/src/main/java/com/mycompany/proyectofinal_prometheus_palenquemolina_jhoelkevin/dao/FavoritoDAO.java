/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
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
public class FavoritoDAO {
    // Inserta un favorito
    public boolean agregarFavorito(int usuarioId, int libroId) {
        String sql = "INSERT INTO favoritos (usuario_id, libro_id) VALUES (?, ?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Elimina un favorito
    public boolean eliminarFavorito(int usuarioId, int libroId) {
        String sql = "DELETE FROM favoritos WHERE usuario_id = ? AND libro_id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtiene la lista de libros favoritos de un usuario
    public List<Libro> getFavoritosByUsuario(int usuarioId) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.* " +
                     "FROM libros l " +
                     "JOIN favoritos f ON l.id = f.libro_id " +
                     "WHERE f.usuario_id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro l = new Libro();
                l.setId(rs.getInt("id"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setPortadaURL(rs.getString("portadaURL"));
                l.setSinopsis(rs.getString("sinopsis"));
                l.setContenido(rs.getString("contenido"));
                lista.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Comprueba si un libro ya es favorito de un usuario
    public boolean esFavorito(int usuarioId, int libroId) {
        String sql = "SELECT * FROM favoritos WHERE usuario_id = ? AND libro_id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
