package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.dto.TransactionMapper;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionMapper mapper;

    public Optional<Transaction> findByProduct(Product product) {
		  return repository.findByProduct(product);
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