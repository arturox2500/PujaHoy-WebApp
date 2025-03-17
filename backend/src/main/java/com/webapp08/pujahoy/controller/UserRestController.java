package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id) { //Get user by id
        return userService.findUser(id);
    }

    
    //@GetMapping("/{id}/image")//Get user image
    

    //@PutMapping("/{id}")//Update user

    
    //@PostMapping("/{id}") //Rate user
    
}