package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.repository.OfertaRepository;

@Service
public class OfertaService {

    @Autowired
	private OfertaRepository repository;
}
