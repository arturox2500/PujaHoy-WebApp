package com.webapp08.pujahoy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.model.Usuario;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginRedirect() {
        return "loginPage"; // Devuelve la vista "loginPage.html"
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam Usuario user) {
        if (usuarioRepository.findByContacto(user.getContacto()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        user.setPass(passwordEncoder.encode(user.getPass()));
        usuarioRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPass())) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}