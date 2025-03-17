package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.service.UserService;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id) { //Get user by id
        return userService.findUser(id);
    }

    
    @GetMapping("/{id}/image")//Get user image
    public ResponseEntity<Object> getPostImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = userService.getPostImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

    //@PutMapping("/{id}")//Update user

    
    //@PostMapping("/{id}") //Rate user
    
}