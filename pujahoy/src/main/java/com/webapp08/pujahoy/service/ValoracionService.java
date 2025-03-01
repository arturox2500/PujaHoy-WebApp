package com.webapp08.pujahoy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.model.Valoracion;
import com.webapp08.pujahoy.repository.ValoracionRepository;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository repository;

    public void save(Valoracion val) {
      repository.save(val);
    }

    public Optional<Valoracion> findByProducto(Producto product) {
		  return repository.findByProducto(product);
	  }

    public List<Valoracion> findAllByVendedor(Usuario user) {
		  return repository.findAllByVendedor(user);
	  }
}
