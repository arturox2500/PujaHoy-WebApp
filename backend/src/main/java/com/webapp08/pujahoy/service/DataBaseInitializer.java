package com.webapp08.pujahoy.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Blob;
import java.sql.Date;
import java.util.Calendar;
import java.io.ByteArrayOutputStream;

import org.hibernate.engine.jdbc.BlobProxy;
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

	public Blob saveImageFromFile(String resourcePath) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
        if (inputStream == null) {
            throw new IOException("File not found in classpath: " + resourcePath);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        
        return BlobProxy.generateProxy(buffer.toByteArray());
    }
}

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

			Calendar calendar = Calendar.getInstance();
			calendar.set(2025, Calendar.FEBRUARY, 24); 
			Date fecha24 = new Date(calendar.getTimeInMillis());

			calendar.set(2025, Calendar.FEBRUARY, 25);
			Date fecha25 = new Date(calendar.getTimeInMillis()); 

			calendar.set(2025, Calendar.MARCH, 25);
			Date fecha25M = new Date(calendar.getTimeInMillis());

			Producto product1 = new Producto("Televisión LG", "Smart TV 4K de 55 pulgadas con HDR y sonido envolvente.", 129.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product01.jpg"), user2);
			Producto product2 = new Producto("Monedas europeas", "Colección de monedas antiguas de distintos países de Europa.", 199.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product02.jpg"), user3);
			Producto product3 = new Producto("Iphone 13", "iPhone 13 con 128GB, cámara dual y chip A15 Bionic en excelente estado.", 359.99, fecha24, fecha25, "Finalizado", saveImageFromFile("static/img/product03.jpg"), user3);		
			Producto product4 = new Producto("Ordenador para ofimática", "PC ideal para oficina con procesador i5, 8GB RAM y SSD de 256GB.", 399.99, fecha24, fecha25, "Finalizado", saveImageFromFile("static/img/product04.jpg"), user4);
			
			Oferta nuevaOferta = new Oferta(user2, product3, 360.00, fecha24);
			product3.getOfertas().add(nuevaOferta);

			Oferta nuevaOferta1 = new Oferta(user3, product4, 400.00, fecha24);
			product4.getOfertas().add(nuevaOferta);

			Oferta nuevaOferta2 = new Oferta(user4, product3, 450.00, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta3 = new Oferta(user2, product3, 460.00, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta4 = new Oferta(user4, product3, 430.00, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta5 = new Oferta(user2, product3, 500.00, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta6 = new Oferta(user4, product3, 570.00, fecha24);
			product3.getOfertas().add(nuevaOferta);
			Oferta nuevaOferta7 = new Oferta(user2, product3, 800.00, fecha24);
			product3.getOfertas().add(nuevaOferta);

			productoRepository.save(product1);
			productoRepository.save(product2);
			productoRepository.save(product3);
			productoRepository.save(product4);

			ofertaRepository.save(nuevaOferta);
			ofertaRepository.save(nuevaOferta1);
			ofertaRepository.save(nuevaOferta2);
			ofertaRepository.save(nuevaOferta3);
			ofertaRepository.save(nuevaOferta4);
			ofertaRepository.save(nuevaOferta5);
			ofertaRepository.save(nuevaOferta6);
			ofertaRepository.save(nuevaOferta7);
			
            Transaccion transaccion1= new Transaccion(product3,user3,user2,800.00);
			Transaccion transaccion2= new Transaccion(product4,user4,user3,400.00);

			transaccionRepository.save(transaccion1);
			transaccionRepository.save(transaccion2);

			Producto product5 = new Producto("Teclado mecánico", "Teclado gaming RGB con switches mecánicos y respuesta rápida.", 79.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product05.webp"), user2);
			Producto product6 = new Producto("Gafas de sol", "Gafas polarizadas con protección UV400, diseño elegante.", 49.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product06.jpg"), user2);
			Producto product7 = new Producto("Mando PS5", "Control inalámbrico DualSense con vibración háptica y gatillos adaptativos.", 69.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product07.avif"), user2);
			Producto product8 = new Producto("Xbox Series X", "Consola de nueva generación con 1TB de almacenamiento y 4K real.", 559.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product08.webp"), user2);
			Producto product9 = new Producto("Ratón para ordenador", "Ratón ergonómico con sensor óptico de alta precisión.", 39.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product09.jpg"), user2);
			Producto product10 = new Producto("Rolex Presidential", "Reloj de lujo con caja de oro y movimiento automático.", 25259.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product10.jpg"), user2);
			Producto product11 = new Producto("Bugatti La Voiture Noire", "Hiperdeportivo exclusivo de 1500 CV, edición limitada.", 1999999.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product11.jpg"), user2);
			Producto product12 = new Producto("Jarrones decorativos", "Set de jarrones de cerámica con acabado artesanal.", 49.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product12.jpg"), user2);
			Producto product13 = new Producto("Bolso Gucci", "Bolso de piel auténtica con diseño clásico y distintivo Gucci.", 1849.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product13.jpg"), user2);
			Producto product14 = new Producto("Vino cosecha 2008", "Vino tinto añejo con notas afrutadas y envejecido en barrica.", 129.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product14.webp"), user2);
			Producto product15 = new Producto("Barquito en botella", "Miniatura artesanal de barco dentro de una botella de cristal.", 59.99, fecha24, fecha25M, "En curso", saveImageFromFile("static/img/product15.jpg"), user2);
			
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