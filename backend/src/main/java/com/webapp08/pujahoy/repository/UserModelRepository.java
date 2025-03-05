package com.webapp08.pujahoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;

@Repository
public interface UserModelRepository extends JpaRepository<UserModel, Long> {
    
    Optional<UserModel> findByName(String name);
    Optional<UserModel> findByContact(String email);
    Optional<UserModel> findByProducts(Product product);
}
   
