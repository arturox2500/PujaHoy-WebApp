package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.repository.UsuarioRepository;
import com.webapp08.pujahoy.repository.OfertaRepository;
import com.webapp08.pujahoy.repository.ProductoRepository;
import com.webapp08.pujahoy.repository.TransaccionRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UsuarioRepository userRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private TransaccionRepository transaccionRepository;

	@Autowired
	private OfertaRepository ofertaRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    @PostConstruct
	public void init() throws IOException, URISyntaxException {
			Usuario user1 = new Usuario("Juan", 5, "Juanito", "juanElGrande@gmail.com", 28001,"descripción prueba arturo", true, passwordEncoder.encode("pass"), "ADMIN");
			Usuario user2 = new Usuario("Pedro", 2, "Pedrito", "pedrosimple@gmail.com", 28024,"descripcion", true, passwordEncoder.encode("pass"), "USER");
			Usuario user3 = new Usuario("Pablo", 3, "Pablito", "pablo@gmail.com", 28012,"descripcion", true, passwordEncoder.encode("pass"), "USER");
			Usuario user4 = new Usuario("Diego", 1, "Diegote", "Diege@gmail.com", 28044,"descripcion la parte", true, passwordEncoder.encode("pass"), "USER");

			userRepository.save(user1);
			userRepository.save(user2);
			userRepository.save(user3);
			userRepository.save(user4);

			//son fehcas de prueba
			Calendar calendar = Calendar.getInstance();
			calendar.set(2025, Calendar.FEBRUARY, 24); 
			Date fecha24 = new Date(calendar.getTimeInMillis());

			calendar.set(2025, Calendar.FEBRUARY, 25);
			Date fecha25 = new Date(calendar.getTimeInMillis()); 

			calendar.set(2025, Calendar.MARCH, 25);
			Date fecha25M = new Date(calendar.getTimeInMillis());
			//son fechas de prueba

			Producto product1 = new Producto("Producto1","mola mucho",900,fecha24,fecha25M,"En curso", user2);
			Producto product2 = new Producto("Producto2","mola nada",840,fecha24,fecha25M,"En curso", user3);
			Producto product3 = new Producto("Ordenador To Guapo","lo a utilizado CR7",500000,fecha24,fecha25,"Finalizado", user3);
			Producto product4 = new Producto("Ordenador To Guapo 2 ","lo a utilizado messi",500000,fecha24,fecha25,"Finalizado", user4);

			Oferta nuevaOferta = new Oferta(user2, product3, 800000, fecha24);
			product3.getOfertas().add(nuevaOferta);

			Oferta nuevaOferta1 = new Oferta(user3, product4, 800000, fecha24);
			product4.getOfertas().add(nuevaOferta);

			productoRepository.save(product1);
			productoRepository.save(product2);
			productoRepository.save(product3);
			productoRepository.save(product4);

			ofertaRepository.save(nuevaOferta);
			ofertaRepository.save(nuevaOferta1);
			
            Transaccion transaccion1= new Transaccion(product3,user3,user2,800000);
			Transaccion transaccion2= new Transaccion(product4,user4,user3,800000);

			transaccionRepository.save(transaccion1);
			transaccionRepository.save(transaccion2);
	}
}
