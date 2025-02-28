package com.webapp08.pujahoy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.webapp08.pujahoy.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.webapp08.pujahoy.model.Usuario;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
		} else {
			model.addAttribute("logged", false);
		}
	}

    @GetMapping("/login")
    public String loginRedirect() {
        return "login"; // Devuelve la vista "loginPage.html"
    }

    @PostMapping("/register")
        public String register(@RequestParam String email, @RequestParam String password, @RequestParam int codigoPostal, @RequestParam String nombre, @RequestParam String nombreVisible, @RequestParam String tipo, @RequestParam String descripcion, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (usuarioRepository.findByContacto(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email ya registrado");
            return "login"; // Redirige al formulario de registro con error
            
        }

        Usuario user = new Usuario(nombre, 0, nombreVisible, email, codigoPostal, descripcion, true, "", "USER");
        user.setPass(passwordEncoder.encode(password));
        usuarioRepository.save(user);
        return "index"; // Redirige a la página principal con sesión iniciada
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index"; // Redirige a la página principal
    } 
}