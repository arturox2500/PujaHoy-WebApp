package com.webapp08.pujahoy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository repository;

    public void save(Rating val) {
      repository.save(val);
    }

    public Optional<Rating> findByProduct(Product product) {
		  return repository.findByProduct(product);
	  }

    public List<Rating> findAllBySeller(UserModel user) {
		  return repository.findAllBySeller(user);
	  }

    public void deleteById(Long id){
      deleteById(id);
    }
}
