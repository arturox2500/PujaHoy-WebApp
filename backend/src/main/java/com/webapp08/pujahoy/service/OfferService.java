package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.repository.OfferRepository;

@Service
public class OfferService {

    @Autowired
	private OfferRepository repository;

    public void save(Offer offer) {
		repository.save(offer);
	}
    public Offer findLastOfferByProduct(long id_product){
        return repository.findLastOfferByProduct(id_product);
    }
    public void deleteById(long id){
        repository.deleteById(id);
    }
    public void delete(Offer offer){
        repository.delete(offer);
    }
}
