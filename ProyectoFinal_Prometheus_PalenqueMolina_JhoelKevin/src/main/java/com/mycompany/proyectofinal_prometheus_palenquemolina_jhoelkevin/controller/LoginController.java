/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.controller;

import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.dao.UsuarioDAO;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Usuario;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.view.VentanaLogin;

/**
 *
 * @author isard
 */
public class LoginController {
    private final VentanaLogin loginView;
    private final UsuarioDAO usuarioDAO;

    /**
     * Constructor que recibe la vista de login.
     * @param loginView referencia a la ventana de login
     */
    public LoginController(VentanaLogin loginView) {
        this.loginView = loginView;
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta autenticar al usuario con el email y contraseña dados
     */
    public Usuario autenticar(String email, String contrasenia) {
        return usuarioDAO.login(email, contrasenia);
    }
}
