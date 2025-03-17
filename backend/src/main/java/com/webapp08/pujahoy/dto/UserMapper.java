package com.webapp08.pujahoy.dto;

import com.webapp08.pujahoy.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // UserModel to PublicUserDTO
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "reputation", target = "reputation"),
        @Mapping(source = "visibleName", target = "visibleName"),
        @Mapping(source = "contact", target = "contact"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "zipCode", target = "zipCode")
    })
    PublicUserDTO toDTO(UserModel userModel);

    //PublicUserDTO to UserModel
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "reputation", target = "reputation"),
        @Mapping(source = "visibleName", target = "visibleName"),
        @Mapping(source = "contact", target = "contact"),
        @Mapping(source = "description", target = "description"),
        @Mapping(target = "profilePic", ignore = true),
        @Mapping(source = "zipCode", target = "zipCode"),
        @Mapping(target = "products", ignore = true),
        @Mapping(target = "encodedPassword", ignore = true),
        @Mapping(target = "pass", ignore = true),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "rols", ignore = true)
    })
    UserModel toDomain(PublicUserDTO publicUserDTO);

    @org.mapstruct.Named("blobToBoolean")
    default boolean blobToBoolean(java.sql.Blob blob) {
        return blob != null;
    }
}
