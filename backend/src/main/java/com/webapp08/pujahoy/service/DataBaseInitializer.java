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

import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.repository.UserModelRepository;
import com.webapp08.pujahoy.repository.OfferRepository;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;


@Service
public class DataBaseInitializer {

    @Autowired
	private UserModelRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private OfferRepository offerRepository;

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
			UserModel user1 = new UserModel("Juan", 5, "Juanito", "juanElGrande@gmail.com", 28001, "", true, passwordEncoder.encode("pass"), "ADMIN");
			UserModel user2 = new UserModel("Pedro", 2, "Pedrito", "pedrosimple@gmail.com", 28024, "Dedicated seller offering high-quality products at affordable prices.", true, passwordEncoder.encode("pass"), "USER");
			UserModel user3 = new UserModel("Pablo", 3, "Pablito", "pablo@gmail.com", 28012, "Specializes in unique and handcrafted items, ensuring customer satisfaction.", true, passwordEncoder.encode("pass"), "USER");
			UserModel user4 = new UserModel("Diego", 1, "Diegote", "Diege@gmail.com", 28044, "Known for his excellent service and a wide variety of premium products.", true, passwordEncoder.encode("pass"), "USER");
			userRepository.save(user1);
			userRepository.save(user2);
			userRepository.save(user3);
			userRepository.save(user4);

			Calendar calendar = Calendar.getInstance();
			calendar.set(2025, Calendar.FEBRUARY, 24); 
			Date date24 = new Date(calendar.getTimeInMillis());

			calendar.set(2025, Calendar.FEBRUARY, 25);
			Date date25 = new Date(calendar.getTimeInMillis()); 

			calendar.set(2025, Calendar.MARCH, 25);
			Date date25M = new Date(calendar.getTimeInMillis());

			Product product1 = new Product("Television LG", "Smart TV 4K 55 inches with HDR and surround sound.", 129.99, date24, date25M, "In progress", saveImageFromFile("static/img/product01.jpg"), user2);
			Product product2 = new Product("European coins", "Collection of old coins from different European countries.", 199.99, date24, date25M, "In progress", saveImageFromFile("static/img/product02.jpg"), user3);
			Product product3 = new Product("Iphone 13", "iPhone 13 with 128GB, dual camera and A15 Bionic chip in excellent condition.", 359.99, date24, date25, "Finished", saveImageFromFile("static/img/product03.jpg"), user3);		
			Product product4 = new Product("Office computer", "PC ideal for office with i5 processor, 8GB RAM and 256GB SSD.", 399.99, date24, date25, "Finished", saveImageFromFile("static/img/product04.jpg"), user4);
			
			Offer newOffer = new Offer(user2, product3, 360.00, date24);
			product3.getOffers().add(newOffer);

			Offer newOffer1 = new Offer(user3, product4, 400.00, date24);
			product4.getOffers().add(newOffer);

			Offer newOffer2 = new Offer(user4, product3, 450.00, date24);
			product3.getOffers().add(newOffer);
			Offer newOffer3 = new Offer(user2, product3, 460.00, date24);
			product3.getOffers().add(newOffer);
			Offer newOffer4 = new Offer(user4, product3, 430.00, date24);
			product3.getOffers().add(newOffer);
			Offer newOffer5 = new Offer(user2, product3, 500.00, date24);
			product3.getOffers().add(newOffer);
			Offer newOffer6 = new Offer(user4, product3, 570.00, date24);
			product3.getOffers().add(newOffer);
			Offer newOffer7 = new Offer(user2, product3, 800.00, date24);
			product3.getOffers().add(newOffer);

			productRepository.save(product1);
			productRepository.save(product2);
			productRepository.save(product3);
			productRepository.save(product4);

			offerRepository.save(newOffer);
			offerRepository.save(newOffer1);
			offerRepository.save(newOffer2);
			offerRepository.save(newOffer3);
			offerRepository.save(newOffer4);
			offerRepository.save(newOffer5);
			offerRepository.save(newOffer6);
			offerRepository.save(newOffer7);
			
            Transaction transaction1 = new Transaction(product3, user3, user2, 800.00);
			Transaction transaction2 = new Transaction(product4, user4, user3, 400.00);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);

			Product product5 = new Product("Mechanical keyboard", "Gaming RGB keyboard with mechanical switches and fast response.", 79.99, date24, date25M, "In progress", saveImageFromFile("static/img/product05.webp"), user2);
			Product product6 = new Product("Sunglasses", "Polarized sunglasses with UV400 protection, elegant design.", 49.99, date24, date25M, "In progress", saveImageFromFile("static/img/product06.jpg"), user2);
			Product product7 = new Product("PS5 controller", "Wireless DualSense controller with haptic feedback and adaptive triggers.", 69.99, date24, date25M, "In progress", saveImageFromFile("static/img/product07.avif"), user2);
			Product product8 = new Product("Xbox Series X", "Next-gen console with 1TB storage and true 4K.", 559.99, date24, date25M, "In progress", saveImageFromFile("static/img/product08.webp"), user2);
			Product product9 = new Product("Computer mouse", "Ergonomic mouse with high-precision optical sensor.", 39.99, date24, date25M, "In progress", saveImageFromFile("static/img/product09.jpg"), user2);
			Product product10 = new Product("Rolex Presidential", "Luxury watch with gold case and automatic movement.", 2499.99, date24, date25M, "In progress", saveImageFromFile("static/img/product10.jpg"), user2);
			Product product11 = new Product("Bugatti La Voiture Noire", "Exclusive hypercar with 1500 HP, limited edition.", 1999999.99, date24, date25M, "In progress", saveImageFromFile("static/img/product11.jpg"), user2);
			Product product12 = new Product("Decorative vases", "Set of ceramic vases with handcrafted finish.", 49.99, date24, date25M, "In progress", saveImageFromFile("static/img/product12.jpg"), user2);
			Product product13 = new Product("Gucci bag", "Authentic leather bag with classic design and Gucci distinctive.", 1499.99, date24, date25M, "In progress", saveImageFromFile("static/img/product13.jpg"), user2);
			Product product14 = new Product("Wine vintage 2008", "Aged red wine with fruity notes and barrel-aged.", 129.99, date24, date25M, "In progress", saveImageFromFile("static/img/product14.webp"), user2);
			Product product15 = new Product("Ship in a bottle", "Handcrafted miniature ship inside a glass bottle.", 59.99, date24, date25M, "In progress", saveImageFromFile("static/img/product15.jpg"), user4);
			
			productRepository.save(product5);
			productRepository.save(product6);
			productRepository.save(product7);
			productRepository.save(product8);
			productRepository.save(product9);
			productRepository.save(product10);
			productRepository.save(product11);
			productRepository.save(product12);
			productRepository.save(product13);
			productRepository.save(product14);
			productRepository.save(product15);
	}
}