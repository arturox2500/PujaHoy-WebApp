package com.webapp08.pujahoy.service;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.OfferDTO;
import com.webapp08.pujahoy.dto.OfferMapper;
import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.RatingDTO;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.OfferRepository;

@Service
public class OfferService {

    @Autowired
	private OfferRepository repository;

    @Autowired
	private OfferMapper mapper;

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

    public OfferDTO toDTO(Offer offer) {
  
      return mapper.toDTO(offer);
    }

    private Offer toDomain(OfferDTO offerDTO){
		return mapper.toDomain(offerDTO);
	}
}
