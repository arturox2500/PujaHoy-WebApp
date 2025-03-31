package com.webapp08.pujahoy.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.RatingDTO;
import com.webapp08.pujahoy.dto.RatingMapper;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.RatingRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;
import com.webapp08.pujahoy.repository.UserModelRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository repository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserModelRepository userRepository;

    @Autowired
    private RatingMapper mapper;

    public void save(Rating val) {
      repository.save(val);
    }

    public Optional<Rating> findById(Long id) {
		  return repository.findById(id);
	  }

    public Optional<RatingDTO> findByProduct(ProductDTO productDTO) {
      Optional<Product> product = productRepository.findById(productDTO.getId());
      Optional<Rating> rating = repository.findByProduct(product.get());
      if (rating.isEmpty()) {
        return Optional.empty();
      }
		  return Optional.of(mapper.toDTO(rating.get()));
	  }

    public List<RatingDTO> findAllBySeller(UserModel user) {
      List<Rating> ratings = repository.findAllBySeller(user);
      if (ratings.isEmpty()) {
        return List.of();
      }
		  return ratings.stream().map(mapper::toDTO).collect(Collectors.toList());
	  }

    public void deleteById(Long id){
      deleteById(id);
    }

    public RatingDTO createRating(int rating, UserModel seller, Product product) {

      Rating newRating = new Rating(seller, product, rating);
      
      repository.save(newRating);
  
      return mapper.toDTO(newRating);
    }

    public void updateRating(UserModel user) { // Responsible for updating the reputation of a user
      List<Rating> ratings = repository.findAllBySeller(user);
      if (ratings.isEmpty()) {
        return;
      }
      int amount = 0;
      for (Rating val : ratings) {
        amount += val.getRating();
      }
      double mean = (double) amount / ratings.size();
  
      user.setReputation(mean);
      userRepository.save(user);
    }

    public Optional<?> rateProduct(int rating, ProductDTO product, PublicUserDTO user){
      if (rating >=1 && rating <=5){
        Optional<Product> prod = productRepository.findById(product.getId());
        Optional<Rating> test = repository.findByProduct(prod.get());
        if (test.isPresent()){
          return Optional.of(3);//The product is already rated
        }
        Optional<Transaction> trans = transactionRepository.findByProduct(prod.get());
        if (trans.isPresent()){
          if(user.getId() == trans.get().getBuyer().getId()) {
            Rating rate = new Rating(prod.get().getSeller(),prod.get(),rating);
            repository.save(rate);
            this.updateRating(trans.get().getSeller());
            return Optional.of(mapper.toDTO(rate));
          } else {
            return Optional.of(2);//You are not the buyer 
          }
        } else {
          return Optional.of(1); //Transaction not found
        }
      }
      return Optional.of(0); //The rating must be between 1 and 5
    }

}
