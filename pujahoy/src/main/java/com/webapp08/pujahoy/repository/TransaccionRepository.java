package com.webapp08.pujahoy.repository;

import java.util.Optional;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Transaccion;

 @Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Optional<Transaccion> findByProducto(Optional<Producto> product);
}