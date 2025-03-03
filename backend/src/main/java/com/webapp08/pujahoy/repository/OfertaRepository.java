package com.webapp08.pujahoy.repository;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webapp08.pujahoy.model.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    @Query("SELECT o FROM Oferta o WHERE o.producto.id = :id_producto ORDER BY o.hora DESC LIMIT 1")
    Oferta findLastOfferByProduct(@Param("id_producto") long id_producto);
    void deleteById(long id);
    
    
}