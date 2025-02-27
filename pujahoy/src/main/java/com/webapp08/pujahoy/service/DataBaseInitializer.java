package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UsuarioRepository UserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {
			Usuario user1 = new Usuario("Juan", 5, "Juanito", "juanElGrande@gmail.com", "descripción prueba arturo", true, passwordEncoder.encode("pass"), "ADMIN");
			Usuario user2 = new Usuario("Pedro", 2, "Pedrito", "pedrosimple@gmail.com", "descripcion", true, passwordEncoder.encode("pass"), "USER");

			UserRepository.save(user1);
			UserRepository.save(user2);

	}
}
