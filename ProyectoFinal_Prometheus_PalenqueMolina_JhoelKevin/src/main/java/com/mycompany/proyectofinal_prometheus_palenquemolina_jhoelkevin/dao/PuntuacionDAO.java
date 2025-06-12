/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author isard
 */
public class PuntuacionDAO {
    /** Inserta o actualiza la puntuación de un usuario para un libro */
    public boolean upsert(int usuarioId, int libroId, int puntuacion) {
        String sql = 
          "INSERT INTO puntuaciones(usuario_id, libro_id, puntuacion) " +
          "VALUES (?,?,?) " +
          "ON DUPLICATE KEY UPDATE puntuacion = VALUES(puntuacion), fecha = CURRENT_TIMESTAMP";
        try (Connection c = ConexionMySQL.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, libroId);
            ps.setInt(3, puntuacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Devuelve la media de puntuaciones por libro */
    public double obtenerMedia(int libroId) {
        String sql = "SELECT AVG(puntuacion) AS media FROM puntuaciones WHERE libro_id = ?";
        try (Connection c = ConexionMySQL.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
