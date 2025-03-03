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
			Usuario user1 = new Usuario("Juan", 5, "Juanito", "juanElGrande@gmail.com", 28001,"descripción prueba arturo", true, passwordEncoder.encode("pass"), "ADMIN", "USER");
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

			Producto product1 = new Producto("Producto1","mola mucho",900,fecha24,fecha25M,"En curso",null, user2);
			Producto product2 = new Producto("Producto2","mola nada",840,fecha24,fecha25M,"En curso",null, user3);
			Producto product3 = new Producto("Ordenador To Guapo","lo a utilizado CR7",500000,fecha24,fecha25,"Finalizado",null, user3);
			Producto product4 = new Producto("Ordenador To Guapo 2 ","lo a utilizado messi",500000,fecha24,fecha25,"Finalizado",null, user4);

			Oferta nuevaOferta = new Oferta(user2, product3, 800000, fecha24);
			product3.getOfertas().add(nuevaOferta);

			Oferta nuevaOferta1 = new Oferta(user3, product4, 800000, fecha24);
			product4.getOfertas().add(nuevaOferta);

			Oferta nuevaOferta2 = new Oferta(user2, product3, 810000, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta3 = new Oferta(user2, product3, 890000, fecha24);
			product3.getOfertas().add(nuevaOferta);

			productoRepository.save(product1);
			productoRepository.save(product2);
			productoRepository.save(product3);
			productoRepository.save(product4);

			ofertaRepository.save(nuevaOferta);
			ofertaRepository.save(nuevaOferta1);
			ofertaRepository.save(nuevaOferta2);
			ofertaRepository.save(nuevaOferta3);
			
            Transaccion transaccion1= new Transaccion(product3,user3,user2,800000);
			Transaccion transaccion2= new Transaccion(product4,user4,user3,800000);

			transaccionRepository.save(transaccion1);
			transaccionRepository.save(transaccion2);

			Producto product5 = new Producto("Producto5", "Me encanta", 950, fecha24, fecha25M, "En curso", null, user2);
			Producto product6 = new Producto("Producto6", "Totalmente recomendable", 1100, fecha24, fecha25M, "En curso", null, user2);
			Producto product7 = new Producto("Producto7", "Perfecto para todo", 1400, fecha24, fecha25M, "En curso", null, user2);
			Producto product8 = new Producto("Producto8", "Lo mejor del mercado", 2000, fecha24, fecha25M, "En curso", null, user2);
			Producto product9 = new Producto("Producto9", "Muy buena opción", 1300, fecha24, fecha25M, "En curso", null, user2);
			Producto product10 = new Producto("Producto10", "Totalmente recomendable", 1050, fecha24, fecha25M, "En curso", null, user2);
			Producto product11 = new Producto("Producto11", "Calidad premium", 2200, fecha24, fecha25M, "En curso", null, user2);
			Producto product12 = new Producto("Producto12", "Lo usarás a diario", 850, fecha24, fecha25M, "En curso", null, user2);
			Producto product13 = new Producto("Producto13", "No te arrepentirás", 1300, fecha24, fecha25M, "En curso", null, user2);
			Producto product14 = new Producto("Producto14", "Muy práctico y duradero", 1400, fecha24, fecha25M, "En curso", null, user2);
			Producto product15 = new Producto("Producto15", "Innovador y moderno", 1600, fecha24, fecha25M, "En curso", null, user2);

			productoRepository.save(product5);
			productoRepository.save(product6);
			productoRepository.save(product7);
			productoRepository.save(product8);
			productoRepository.save(product9);
			productoRepository.save(product10);
			productoRepository.save(product11);
			productoRepository.save(product12);
			productoRepository.save(product13);
			productoRepository.save(product14);
			productoRepository.save(product15);


	}
}
