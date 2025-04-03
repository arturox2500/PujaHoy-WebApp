package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.dto.TransactionMapper;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionMapper mapper;

    @Lazy
    @Autowired
    private ProductRepository productRepository;

    public Optional<Transaction> findByProductOLD(Product product) {
		  return repository.findByProduct(product);
	  }
    public Optional<TransactionDTO> findByProduct(long id_product) {
      Optional<Product> product=productRepository.findById(id_product);
		  Optional<Transaction> trans=repository.findByProduct(product.get());
      if(trans.isPresent()){
        return Optional.of(mapper.toDTO(trans.get()));
      }
      return Optional.empty();
	  }


    public Transaction save(Transaction transaction) {
      return repository.save(transaction);
    }

    public void deleteById(long id) {
      repository.deleteById(id);
    }

    public TransactionDTO findTransactionDTO(Product product) {
		  return mapper.toDTO(repository.findByProduct(product).get());
	  }

}