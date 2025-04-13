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
        @Mapping(source = "zipCode", target = "zipCode"),
        @Mapping(target = "image", expression = "java(generateImageUrl(userModel.getId()))"),
        @Mapping(source = "rols", target = "rols"),
        @Mapping(source = "active", target = "active"),
    })
    PublicUserDTO toDTO(UserModel userModel);

    default String generateImageUrl(Long id) {
        return "/users/" + id + "/image";
    }
    
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
        @Mapping(source = "image", target = "image"),
        @Mapping(target = "products", ignore = true),
        @Mapping(target = "encodedPassword", ignore = true),
        @Mapping(target = "pass", ignore = true),
        @Mapping(target = "rols", ignore = true),
        @Mapping(source = "active", target = "active")
    })
    UserModel toDomain(PublicUserDTO publicUserDTO);

    //UserDTO to UserModel
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(source = "username", target = "name"),
        @Mapping(target = "reputation", ignore = true),
        @Mapping(source = "visibleName", target = "visibleName"),
        @Mapping(source = "email", target = "contact"),
        @Mapping(source = "description", target = "description"),
        @Mapping(target = "profilePic", ignore = true),
        @Mapping(source = "zipCode", target = "zipCode"),
        @Mapping(target = "image", ignore = true),
        @Mapping(target = "products", ignore = true),
        @Mapping(source="password",target = "encodedPassword"),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "rols", ignore = true),
        @Mapping(target = "pass", ignore = true)
    })
    UserModel toUserModel(UserDTO userDTO);

    //UserEditDTO to UserModel
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "contact", target = "contact"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "zipCode", target = "zipCode"),
        @Mapping(target = "active", ignore = true),
        @Mapping(target = "image", ignore = true),
        @Mapping(target = "name", ignore = true),
        @Mapping(target = "pass", ignore = true),
        @Mapping(target = "products", ignore = true),
        @Mapping(target = "profilePic", ignore = true),
        @Mapping(target = "reputation", ignore = true),
        @Mapping(target = "rols", ignore = true),
        @Mapping(target = "visibleName", ignore = true),
        @Mapping(target = "encodedPassword", ignore = true)
    })
    UserModel toUserModelFromEdit(UserEditDTO userEditDTO);
}
