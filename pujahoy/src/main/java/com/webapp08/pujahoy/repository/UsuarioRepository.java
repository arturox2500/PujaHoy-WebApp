package com.webapp08.pujahoy.repository;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.webapp08.pujahoy.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByContacto(String contacto);

    Optional<Usuario> findByEmail(String email);
    
}
   
