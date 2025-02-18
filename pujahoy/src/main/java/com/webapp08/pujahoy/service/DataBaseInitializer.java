package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.webapp08.pujahoy.model.Usuario;
//import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.repository.OfertaRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UsuarioRepository UserRepository;

	@Autowired
	private OfertaRepository OfertRepository;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {

    }
}
