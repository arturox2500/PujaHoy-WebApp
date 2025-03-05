package com.webapp08.pujahoy.repository;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webapp08.pujahoy.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o WHERE o.product.id = :id_product ORDER BY o.hour DESC LIMIT 1")
    Offer findLastOfferByProduct(@Param("id_product") long id_product);
    void deleteById(long id);
    
    
}