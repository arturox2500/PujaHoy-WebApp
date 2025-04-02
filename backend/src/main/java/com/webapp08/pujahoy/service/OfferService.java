package com.webapp08.pujahoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.OfferDTO;
import com.webapp08.pujahoy.dto.OfferMapper;
import com.webapp08.pujahoy.model.Offer;
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
    public OfferDTO findLastOfferByProduct(long id_product){
        return mapper.toDTO(repository.findLastOfferByProduct(id_product));
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

    public Offer toDomain(OfferDTO offerDTO){
		return mapper.toDomain(offerDTO);
	}

    public List<OfferDTO> toDTOs(List<Offer> offersDTO){
		return mapper.toDTOList(offersDTO);
	}
}
