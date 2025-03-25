package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.dto.TransactionMapper;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionMapper mapper;

    @Autowired
    private OfferService offerService;

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

    public void createTransaction(Product prod) {
      Offer offer = offerService.findLastOfferByProduct(prod.getId());
      double cost = offer.getCost();
      UserModel buyer = offer.getUser();
      UserModel seller = prod.getSeller();
      Transaction transaction = new Transaction(prod, seller, buyer, cost);
      repository.save(transaction);
    }
}