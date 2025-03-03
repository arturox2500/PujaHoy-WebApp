package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.repository.OfertaRepository;

//import com.webapp08.pujahoy.model.Oferta;
//import com.webapp08.pujahoy.repository.OfertaRepository;

@Service
public class OfertaService {

    @Autowired
	private OfertaRepository repository;

    public void save(Oferta oferta) {
		repository.save(oferta);
	}
    public Oferta findLastOfferByProduct(long id_producto){
        return repository.findLastOfferByProduct(id_producto);
    }
    public void deleteById(long id){
        repository.deleteById(id);
    }
}
