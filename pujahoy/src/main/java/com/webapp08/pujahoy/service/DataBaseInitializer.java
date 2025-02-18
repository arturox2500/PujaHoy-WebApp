package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
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

	//@Autowired
	//private OfertaRepository OfertRepository;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {
			Usuario user1 = new Usuario("1", "Juan", "Juanito", 5, "Usuario registrado", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sollicitudin varius nibh.", "909-50-78-95");
			Usuario user2 = new Usuario("2", "Pedro", "Pedrito", 2, "Usuario registrado", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sollicitudin varius nibh.","909-50-78-95");

			UserRepository.save(user1);
			UserRepository.save(user2);

	}
}
