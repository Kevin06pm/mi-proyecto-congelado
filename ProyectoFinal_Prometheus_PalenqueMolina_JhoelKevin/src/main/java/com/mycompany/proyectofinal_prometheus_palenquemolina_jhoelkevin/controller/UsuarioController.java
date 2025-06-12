/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.UsuarioDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view.VentanaRegister;

/**
 *
 * @author isard
 */
public class UsuarioController {
    private VentanaRegister registerView;
    private final UsuarioDAO usuarioDAO;
    
    
    

    public UsuarioController() {
        
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Constructor que recibe la vista de registro.
     * @param registerView referencia a la ventana de registro
     */
    public UsuarioController(VentanaRegister registerView) {
        this.registerView = registerView;
        this.usuarioDAO = new UsuarioDAO();
    }
    
    

    /**
     * Intenta registrar un nuevo usuario en la base de datos.
     * usuario el objeto Usuario con los datos a registrar
     * si el registro fue exitoso, false si hubo un error o el email ya existe
     */
    public boolean registrarUsuario(Usuario usuario) {
        return usuarioDAO.registrar(usuario);
    }
    
    public Usuario obtenerPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }
    
    
}
