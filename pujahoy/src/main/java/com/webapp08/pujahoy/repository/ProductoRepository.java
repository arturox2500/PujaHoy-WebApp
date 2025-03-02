package com.webapp08.pujahoy.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findById(Long id);
    Page<Producto> findByVendedor_Nombre(String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p " +
       "JOIN Transaccion t ON t.producto = p " +
       "JOIN t.comprador u " +
       "WHERE u.nombre = :compradorNombre")
    Page<Producto> findProductosCompradosPorUsuario(@Param("compradorNombre") String compradorNombre, Pageable pageable);

    Page<Producto> findAll(Pageable pageable);
}
