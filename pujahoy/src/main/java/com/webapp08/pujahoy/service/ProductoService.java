package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
	private ProductoRepository repository;
    
    public Optional<Producto> findById(long id) {
		return repository.findById(id);
	}

	public Optional<Producto> findByDatos(String id) {
		return repository.findByNombre(id);
	}	

	public Producto save(Producto producto) {
		return repository.save(producto);
	}

	public void DeleteById(long id_producto) {
        repository.deleteById(id_producto);
    }

}
