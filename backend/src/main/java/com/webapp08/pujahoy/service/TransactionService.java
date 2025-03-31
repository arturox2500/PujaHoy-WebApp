package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.OfferDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.dto.TransactionMapper;
import com.webapp08.pujahoy.dto.ProductMapper;
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

    @Lazy
    @Autowired
    private ProductService productService;

    public Optional<Transaction> findByProductOLD(Product product) {
		  return repository.findByProduct(product);
	  }
    public TransactionDTO findByProduct(long id_product) {
      Optional<Product> product=productService.findByIdOLD(id_product);
		  Optional<Transaction> trans=repository.findByProduct(product.get());
      if(trans.isPresent()){
        return mapper.toDTO(trans.get());
      }
      return null;
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


    public void createTransaction(long id_product) {
      Offer offer = offerService.findLastOfferByProductOLD(id_product);
      Optional<Product> product = productService.findByIdOLD(id_product);
      double cost = offer.getCost();
      UserModel buyer = offer.getUser();
      UserModel seller = product.get().getSeller();
      Transaction transaction = new Transaction(product.get(), seller, buyer, cost);
      repository.save(transaction);
    }

    public void makeTransaction(Optional<ProductDTO> product, OfferDTO lastOffer) {
        
    }
}