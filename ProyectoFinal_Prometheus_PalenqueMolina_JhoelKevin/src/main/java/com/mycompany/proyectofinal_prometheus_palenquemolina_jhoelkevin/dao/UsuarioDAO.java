/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL;
import static com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL.getConnection;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author isard
 */
public class UsuarioDAO {
    // Inserta un nuevo usuario
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, contrasenia) VALUES (?, ?, ?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getContrasenia());
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Verifica el login de un usuario 
    public Usuario login(String email, String contrasenia) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND contrasenia = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, contrasenia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setContrasenia(rs.getString("contrasenia"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no encontró coincidencias
    }
    
    /**
     * Recupera un usuario por ID
     */
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT id, nombre, email, contrasenia FROM usuarios WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setContrasenia(rs.getString("contrasenia"));
                    return u;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
