/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.database.ConexionMySQL;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Capitulo;
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
public class CapituloDAO {
    
    // Lista de los capitulos
    public List<Capitulo> obtenerPorLibro(int libroId) {
        List<Capitulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM capitulos WHERE libro_id=? ORDER BY numero";
        try (Connection c=ConexionMySQL.getConnection();
             PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Capitulo cap = new Capitulo();
                cap.setId(rs.getInt("id"));
                cap.setLibroId(libroId);
                cap.setNumero(rs.getInt("numero"));
                cap.setTitulo(rs.getString("titulo"));
                cap.setContenido(rs.getString("contenido"));
                lista.add(cap);
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return lista;
    }
}
