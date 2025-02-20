package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.repository.TransaccionRepository;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository repository;

    public Optional<Transaccion> findByProducto_id(long id) {
		return repository.findById(id);
	}
}
