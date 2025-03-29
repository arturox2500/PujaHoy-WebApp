package com.webapp08.pujahoy.dto;

import com.webapp08.pujahoy.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


    // Product -> ProductDTO
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "iniValue", target = "iniValue"),
        @Mapping(source = "iniHour", target = "iniHour"),
        @Mapping(source = "endHour", target = "endHour"),
        @Mapping(source = "state", target = "state"),
        @Mapping(target = "imgURL", expression = "java(generateImageUrl(product.getId()))"), 
        @Mapping(source = "seller", target = "seller") 
    })
    ProductDTO toDTO(Product product);

    default String generateImageUrl(Long id) {
        return "/products/" + id + "/image";  
    }

    // ProductDTO -> Product
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "iniValue", target = "iniValue"),
        @Mapping(source = "iniHour", target = "iniHour"),
        @Mapping(source = "endHour", target = "endHour"),
        @Mapping(source = "state", target = "state"),
        @Mapping(source = "imgURL", target = "imgURL"), 
        @Mapping(target = "image", ignore = true), 
        @Mapping(target = "offers", ignore = true), 
        @Mapping(source =  "seller", target = "seller") 
    })
    Product toDomain(ProductDTO productDTO);
}
