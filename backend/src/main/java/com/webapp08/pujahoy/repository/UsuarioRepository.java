package com.webapp08.pujahoy.repository;

import java.util.Optional;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByNombre(String name);
    Optional<Usuario> findByContacto(String email);
    Optional<Usuario> findByProductos(Producto producto);
}
   
