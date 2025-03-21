package com.webapp08.pujahoy.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webapp08.pujahoy.model.Offer;

@Mapper(componentModel = "spring")
public interface OfferBasicMapper {

    // Offer --------> OfferBasicDTO
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cost", target = "cost")
    OfferBasicDTO toDTO(Offer offer);

    List<OfferBasicDTO> toDTOList(List<Offer> offers);

    //  OfferBasicDTO -------> Offer 
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cost", target = "cost")
    @Mapping(target = "user", ignore = true)    
    @Mapping(target = "product", ignore = true) 
    @Mapping(target = "hour", ignore = true)    
    Offer toDomain(OfferBasicDTO offerBasicDTO);
}
