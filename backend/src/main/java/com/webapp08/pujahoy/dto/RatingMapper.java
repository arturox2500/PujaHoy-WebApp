package com.webapp08.pujahoy.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webapp08.pujahoy.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    //Rating to RatingDTO
    @Mapping(target = "idSeller", expression = "java(rating.getSeller().getId())") 
    @Mapping(target = "idProduct", expression = "java(rating.getProduct().getId())")
    RatingDTO toDTO(Rating rating);

    //RatingDTO to Rating
    @Mapping(source = "idSeller", target = "seller.id")
    @Mapping(source = "idProduct", target = "product.id")
    Rating toDomain(RatingDTO ratingDTO);
}
