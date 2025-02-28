package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.repository.ProductoRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UsuarioRepository userRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {
			Usuario user1 = new Usuario("Juan", 5, "Juanito", "juanElGrande@gmail.com", 28001,"descripción prueba arturo", true, passwordEncoder.encode("pass"), "ADMIN");
			Usuario user2 = new Usuario("Pedro", 2, "Pedrito", "pedrosimple@gmail.com", 28024,"descripcion", true, passwordEncoder.encode("pass"), "USER");
			Usuario user3 = new Usuario("Pablo", 3, "Pablito", "pablo@gmail.com", 28012,"descripcion", true, passwordEncoder.encode("pass"), "USER");

			userRepository.save(user1);
			userRepository.save(user2);
			userRepository.save(user3);
	
			Producto product1 = new Producto("Producto1","mola mucho",900,"En venta", user2);
			Producto product2 = new Producto("Producto2","mola nada",840,"En venta", user3);

			productoRepository.save(product1);
			productoRepository.save(product2);
	}
}
