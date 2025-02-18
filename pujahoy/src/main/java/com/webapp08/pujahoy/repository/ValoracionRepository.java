package com.webapp08.pujahoy.repository;

//import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp08.pujahoy.model.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    
}
