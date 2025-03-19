package com.webapp08.pujahoy.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.webapp08.pujahoy.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    //Rating to RatingDTO
    @Mapping(source = "rating", target = "rating")
    RatingDTO toDTO(Rating rating);

    //RatingDTO to Rating
    @Mappings({
        @Mapping(target = "product", ignore = true),
        @Mapping(target = "seller", ignore = true)
    })
    Rating toDomain(RatingDTO ratingDTO);
}
