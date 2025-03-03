package com.webapp08.pujahoy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

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
    public String register(Model model, @RequestParam String email, @RequestParam String password,
            @RequestParam String codigoPostal, @RequestParam String username, @RequestParam String nombreVisible,
            @RequestParam String descripcion, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (usuarioRepository.findByNombre(username).isPresent() || email.isBlank() || password.isBlank()
                || codigoPostal.isBlank() || username.isBlank() || nombreVisible.isBlank()) {
            model.addAttribute("error", "Wrongs fields or user already exists");
            return "login"; // Redirige al formulario de registro con error
        }

        if (!codigoPostal.matches("\\d{5}") ) {
            model.addAttribute("error", "The zip code must be a 5 digit number");
            return "login"; 
        }

        Usuario user = new Usuario(username, 0, nombreVisible, email, Integer.parseInt(codigoPostal), descripcion, true,
                passwordEncoder.encode(password), "USER");
        usuarioRepository.save(user);

        // Autentica automáticamente al usuario reutilizando el authenticationProvider
        //UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        //Authentication authentication = authenticationProvider.authenticate(authToken);
        //SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/"; // Redirige a la página principal con sesión iniciada
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index"; // Redirige a la página principal
    }
}