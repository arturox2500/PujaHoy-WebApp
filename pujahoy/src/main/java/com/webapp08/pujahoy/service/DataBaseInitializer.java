package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Usuario;
//import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.UsuarioRepository;
//import com.webapp08.pujahoy.repository.OfertaRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UsuarioRepository UserRepository;
	 @Autowired
    private PasswordEncoder passwordEncoder;

	//@Autowired
	//private OfertaRepository OfertRepository;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {
			Usuario user1 = new Usuario( "Juan", "Juanito", 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sollicitudin varius nibh.", "test@test.com",passwordEncoder.encode("1234"),true,new ArrayList<>(List.of("USER")));
			Usuario user2 = new Usuario( "Pedro", "Pedrito", 2, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sollicitudin varius nibh.","test1@test.com",passwordEncoder.encode("1234"),true,new ArrayList<>(List.of("USER")));

			UserRepository.save(user1);
			UserRepository.save(user2);

	}
}
