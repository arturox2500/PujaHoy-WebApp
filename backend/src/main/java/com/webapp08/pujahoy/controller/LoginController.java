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
    private UsuarioRepository userRepository;

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
        return "login";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String email, @RequestParam String password,
            @RequestParam String zipCode, @RequestParam String username, @RequestParam String visibleName,
            @RequestParam String description, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (userRepository.findByNombre(username).isPresent() || email.isBlank() || password.isBlank()
                || zipCode.isBlank() || username.isBlank() || visibleName.isBlank()) {
            model.addAttribute("error", "Wrongs fields or user already exists");
            return "login";
        }

        if (!zipCode.matches("\\d{5}") ) {
            model.addAttribute("error", "The zip code must be a 5 digit number");
            return "login"; 
        }

        Usuario user = new Usuario(username, 0, visibleName, email, Integer.parseInt(zipCode), description, true,
                passwordEncoder.encode(password), "USER");
        userRepository.save(user);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/"; 
    }
}