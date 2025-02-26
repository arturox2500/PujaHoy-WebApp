package com.webapp08.pujahoy.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.ArrayList;
import java.util.Arrays;

import com.webapp08.pujahoy.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.webapp08.pujahoy.model.Usuario;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginRedirect() {
        return "login"; // Devuelve la vista "loginPage.html"
    }

    @PostMapping("/register")
        public String register(@RequestParam String email, @RequestParam String password, @RequestParam String nombre, @RequestParam String nombreVisible, @RequestParam String tipo, @RequestParam String descripcion, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (usuarioRepository.findByContacto(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email ya registrado");
            return "login"; // Redirige al formulario de registro con error
            
        }

        Usuario user = new Usuario(nombre, nombreVisible, 0, descripcion, email, "", true, new ArrayList<>(Arrays.asList("USER")));
        user.setPass(passwordEncoder.encode(password));
        usuarioRepository.save(user);
        return "index"; // Redirige a la página principal con sesión iniciada
}
}