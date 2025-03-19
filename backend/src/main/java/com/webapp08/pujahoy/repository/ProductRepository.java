package com.webapp08.pujahoy.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findById(Long id);
    Page<Product> findBySeller_Name(String name, Pageable pageable);
    List<Product> findBySeller(UserModel user);
    Page<Product> findBySeller_Id(Pageable pageable, Long id);

    @Query("SELECT p FROM Product p " +
       "JOIN Transaction t ON t.product = p " +
       "JOIN t.buyer u " +
       "WHERE u.name = :buyerName")
    Page<Product> findBoughtProductsByUser(@Param("buyerName") String buyerName, Pageable pageable);

    @Query("SELECT p FROM Product p " +
       "JOIN Transaction t ON t.product = p " +
       "JOIN t.buyer u " +
       "WHERE u.id = :buyerId")
    Page<Product> findBoughtProductsByUserID(Pageable pageable, @Param("buyerId") Long buyerId);

    Page<Product> findAll(Pageable pageable);

    //Advanced search algorithm
    @Query("SELECT p FROM Product p ORDER BY p.seller.reputation DESC")
    Page<Product> findAllOrderedBySellerReputation(Pageable pageable);
    
    //Advanced search algorithm only In progress
    @Query("SELECT p FROM Product p WHERE p.state = 'In progress' ORDER BY p.seller.reputation DESC")
    Page<Product> findByStateInProgressOrderedBySellerReputation(Pageable pageable);
}
