package com.webapp08.pujahoy.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.webapp08.pujahoy.model.Offer;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    // Offer --------> OfferDTO
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "cost", target = "cost"),
        @Mapping(source = "hour", target = "hour"),
        @Mapping(source = "user", target = "user"),
        @Mapping(source = "product", target = "product")
    })
    OfferDTO toDTO(Offer offer);

    List<OfferDTO> toDTOList(List<Offer> offers);

    //  OfferDTO -------> Offer 
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "cost", target = "cost"),   
        @Mapping(source = "hour", target = "hour"),
        @Mapping(source = "user", target = "user"),
        @Mapping(source = "product", target = "product")
    }) 
    Offer toDomain(OfferDTO offerDTO);
}
