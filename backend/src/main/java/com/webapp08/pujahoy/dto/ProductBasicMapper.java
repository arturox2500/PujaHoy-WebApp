package com.webapp08.pujahoy.dto;

import com.webapp08.pujahoy.model.Product;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductBasicMapper {

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(target = "imgURL", expression = "java(generateImageUrl(product.getId()))")
    })
    ProductBasicDTO toDTO(Product product);
    List<ProductBasicDTO> toDTOList(List<Product> products);

    default String generateImageUrl(Long id) {
        return "https://localhost:8443/api/v1/products/" + id + "/image";  // URL dinámica de la imagen
    }

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "imgURL", target = "imgURL")
    })
    Product toDomain(ProductBasicDTO productBasicDTO);
}
