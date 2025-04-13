package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.UserEditDTO;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.UserService;
import java.security.Principal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> me(HttpServletRequest request) { //Get his own details
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<PublicUserDTO> user = userService.findByName(principal.getName());
            if (user.isPresent()) {
                PublicUserDTO loggedInUser = user.get();
                return ResponseEntity.ok(loggedInUser);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be authenticated");
    }

    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id, HttpServletRequest request) { // Get user by id
        return  userService.findUser(id);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<?> getUserProducts(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Optional<PublicUserDTO> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        if (!user.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error",
                            "You are not allowed to see another user's product list"));
        }

        PublicUserDTO loggedInUser = user.get();
        if (!loggedInUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductBasicDTO> products = productService.findProductsByUser(pageable, id);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/boughtProducts")
    public ResponseEntity<?> getBoughtProducts(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Optional<PublicUserDTO> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        if (!user.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error",
                            "You are not allowed to publish products for another user"));
        }

        PublicUserDTO loggedInUser = user.get();
        if (!loggedInUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductBasicDTO> boughtProducts = productService.findBoughtProductsByUser(pageable, id);

        return ResponseEntity.ok(boughtProducts);
    }

    @GetMapping("/{id}/image") // Get user image
    public ResponseEntity<Object> getPostImage(@PathVariable long id) throws SQLException, IOException {

        Resource postImage = userService.getPostImage(id);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(postImage);

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> editUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile,HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (!user.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You are not allowed to upload images for another user"));
        }
        
        userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity.ok().build();
    }

    @PutMapping("") // Update user
    public ResponseEntity<?> replaceUserPost(@RequestBody UserEditDTO updatedUserDTO,HttpServletRequest request) throws SQLException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) { 
            Optional<PublicUserDTO> user = userService.findByName(principal.getName());            
            if (user.isPresent()) {
                if (userService.getTypeById(user.get().getId()).equals("Registered User") && user.get().getId() == updatedUserDTO.getId()) {
                    PublicUserDTO userUpdated = userService.replaceUser(updatedUserDTO);
                    if (userUpdated == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not updated due to bad request parameters");
                    }
                    return ResponseEntity.ok(userUpdated);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is only allowed to change his own data");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be authenticated");   
	}

    @PutMapping("/{id}/active") // Banned user
    public ResponseEntity<?> bannedUser(@PathVariable long id, HttpServletRequest request) throws SQLException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<PublicUserDTO> admin = userService.findByName(principal.getName());
            if (admin.isPresent()) {
                Optional<PublicUserDTO> user = userService.findById(id);
                if(user.isPresent()){
                    Optional<PublicUserDTO> newUser = userService.bannedUser(id, admin.get());
                    if (newUser.isPresent()) { //If the user is an administrator he want to ban the user
                        return ResponseEntity.ok(userService.findUser(newUser.get().getId()));
                    } else {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "You are not an admin"));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be authenticated");   
	}
}