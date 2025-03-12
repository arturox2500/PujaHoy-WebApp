package com.webapp08.pujahoy.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.UserModelRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {
	
	@Autowired
	private UserModelRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestParam String email, @RequestParam String password,
                                      @RequestParam String zipCode, @RequestParam String username,
                                      @RequestParam String visibleName, @RequestParam String description) {

        if (userRepository.findByName(username).isPresent() || email.isBlank() || password.isBlank()
                || zipCode.isBlank() || username.isBlank() || visibleName.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Wrong fields or user already exists"));
        }

        if (!zipCode.matches("\\d{5}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "The zip code must be a 5-digit number"));
        }

        UserModel user = new UserModel(username, 0, visibleName, email, Integer.parseInt(zipCode), description, true,
                passwordEncoder.encode(password), "USER");
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
