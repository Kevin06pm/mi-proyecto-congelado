/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.CapituloDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Capitulo;
import java.util.List;

/**
 *
 * @author isard
 */
public class CapituloController {
    private final CapituloDAO dao = new CapituloDAO();
    
    
    public List<Capitulo> obtenerCapitulos(int libroId){
        return dao.obtenerPorLibro(libroId);
    }
}
