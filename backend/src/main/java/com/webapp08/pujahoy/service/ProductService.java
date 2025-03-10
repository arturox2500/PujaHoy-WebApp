package com.webapp08.pujahoy.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
	private ProductRepository repository;
    
    public Optional<Product> findById(long id) {
		return repository.findById(id);
	}

	public Optional<Product> findById(Long id) {
		return repository.findById(id);
	}	

	public Product save(Product product) {
		return repository.save(product);
	}

	public void deleteById(long id_product) {
        repository.deleteById(id_product);
    }

	public List<Product> findBySeller(UserModel user) {
		return repository.findBySeller(user);
	}

	//Search all
    public Page<Product> obtainAllProductOrdersByReputation(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findAllOrderedBySellerReputation(pageable);
    }
	//Search only In progress
	public Page<Product> obtainAllProductOrdersInProgressByReputation(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findByStateInProgressOrderedBySellerReputation(pageable);
    }

	public Page<Product> obtainAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

	public Page<Product> obtainPaginatedProducts(String sellerName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findBySeller_Name(sellerName, pageable);
    }

	public Page<Product> obtainProductsBuyed(String buyerName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findBoughtProductsByUser(buyerName, pageable);
    }

}
