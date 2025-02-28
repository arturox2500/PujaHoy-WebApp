package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Calendar;

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

			long ahora = System.currentTimeMillis(); // Tiempo actual en milisegundos
			Date fechaActual = new Date(ahora); // Convertir a java.sql.Date

			// Sumar un minuto a la fecha actual
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(ahora); // Establecer la fecha actual en el calendario
			calendar.add(Calendar.MINUTE, 1); // Sumar 1 minuto

			// Obtener la nueva fecha con un minuto adicional
			Date fechaConUnMinuto = new Date(calendar.getTimeInMillis()); // Convertir a java.sql.Date
			calendar.add(Calendar.MINUTE, 2); // Sumar 1 minuto
			Date fechaCondosMinuto = new Date(calendar.getTimeInMillis()); // Convertir a java.sql.Date
	
			Producto product1 = new Producto("Producto1","mola mucho",900,fechaActual,fechaConUnMinuto,"En curso", user2);
			Producto product2 = new Producto("Producto2","mola nada",840,fechaActual,fechaCondosMinuto,"En curso", user3);

			productoRepository.save(product1);
			productoRepository.save(product2);
	}
}
