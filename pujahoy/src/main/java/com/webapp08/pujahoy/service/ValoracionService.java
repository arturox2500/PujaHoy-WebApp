package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Valoracion;
import com.webapp08.pujahoy.repository.ValoracionRepository;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository repository;

    public void save(Valoracion val) {
		repository.save(val);
	}
}
