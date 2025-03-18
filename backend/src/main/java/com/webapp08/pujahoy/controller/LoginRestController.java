package com.webapp08.pujahoy.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp08.pujahoy.dto.UserDTO;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.UserModelRepository;

@RestController
@RequestMapping("/api")
public class LoginRestController {
	
	@Autowired
	private UserModelRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/user")
	public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {

                if (userRepository.findByName(userDTO.getUsername()).isPresent() || userDTO.getEmail().isBlank() || userDTO.getPassword().isBlank()
                        || userDTO.getZipCode().isBlank() || userDTO.getUsername().isBlank() || userDTO.getVisibleName().isBlank()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Wrong fields or user already exists"));
                }
            
                if (!userDTO.getZipCode().matches("\\d{5}")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "The zip code must be a 5-digit number"));
                }
            
                UserModel user = new UserModel(userDTO.getUsername(), 0, userDTO.getVisibleName(), userDTO.getEmail(), Integer.parseInt(userDTO.getZipCode()), userDTO.getDescription(), true,
                        passwordEncoder.encode(userDTO.getPassword()), "USER");
                userRepository.save(user);
            
                Map<String, String> response = new HashMap<>();
                response.put("message", "User registered successfully");
            
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
}
