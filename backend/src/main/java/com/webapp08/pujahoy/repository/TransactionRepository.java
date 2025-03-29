package com.webapp08.pujahoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;

 @Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByProduct(Product product);
    //Optional<TransactionDTO> findByProduct(Product product);
}