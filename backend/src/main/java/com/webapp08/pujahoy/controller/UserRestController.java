package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.ProductMapper;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.UserDTO;
import com.webapp08.pujahoy.dto.UserEditDTO;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.TransactionService;
import com.webapp08.pujahoy.service.UserService;
import java.security.Principal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
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
import java.net.URI;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<?> me(HttpServletRequest request) { //Get his own details
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<PublicUserDTO> user = userService.findByName(principal.getName());
            if (user.isPresent()) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be authenticated");
    }

    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id) { // Get user by id
        return userService.findUser(id);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<?> publishProduct(@RequestBody ProductDTO productDTO, HttpServletRequest request,
            @PathVariable Long id) {

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByNameOLD(principal.getName());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        if (!user.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error",
                            "You are not allowed to publish products for another user"));
        }

        UserModel loggedInUser = user.get();
        if (loggedInUser.determineUserType().equals("Administrator")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Administrators are not allowed to publish products"));
        }

        if (!loggedInUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
                productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty() ||
                productDTO.getDuration() == null || productDTO.getIniValue() == null) {

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "All fields must be filled"));
        }

        if (productDTO.getDuration() < 1) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error",
                            "The duration field must contain a number higher or equal to 1."));
        }

        if (productDTO.getIniValue() < 1) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error",
                            "The iniValue field must contain a number higher or equal to 1."));
        }

        try {

            Date iniHour = new Date(System.currentTimeMillis());
            Date endHour = new Date(iniHour.getTime() + (Long) productDTO.getDuration() * 24 * 60 * 60 * 1000);

            Product product = new Product(
                    productDTO.getName(),
                    productDTO.getDescription(),
                    productDTO.getIniValue(),
                    iniHour,
                    endHour,
                    "In progress",
                    null,
                    user.get());

            product.setImgURL("/api/products/" + product.getId() + "/image");
            productService.save(product);

            ProductDTO responseDTO = ProductMapper.INSTANCE.toDTO(product);
            URI location = fromCurrentRequest().path("/products/{id}").buildAndExpand(responseDTO.getId()).toUri();

            return ResponseEntity.created(location).body(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error processing the product: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/products/{pid}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @PathVariable Long pid,
            @RequestBody ProductDTO productDTO, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByNameOLD(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        Optional<Product> optionalProduct = productService.findByIdOLD(pid);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Product product = optionalProduct.get();
        UserModel loggedInUser = user.get();

        if (!loggedInUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }

        Optional<Transaction> trans = transactionService.findByProduct(product);

        if (trans.isPresent()) {
            if (trans.get().getBuyer().getId().equals(loggedInUser.getId())) {
                if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
                        productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty() ||
                        productDTO.getDuration() == null || productDTO.getIniValue() == null) {
                    if (productDTO.getState().equals("Delivered")) {
                        product.setState("Delivered");
                        ProductDTO responseDTO = ProductMapper.INSTANCE.toDTO(product);
                        return ResponseEntity.ok(responseDTO);
                    }
                }
            }
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "The state field contains an incorrect value"));
        } else {
            if (!optionalProduct.get().getOffers().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "You cannot edit a product if a user placed a bid"));
            }
            if (!(product.getSeller().getId().equals(loggedInUser.getId())
                    || loggedInUser.determineUserType().equals("Administrator"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "You do not have permission to modify this product"));
            }

            if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
                    productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty() ||
                    productDTO.getDuration() == null || productDTO.getIniValue() == null) {

                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "All fields must be filled"));
            }

            if (productDTO.getDuration() < 1) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error",
                                "The duration field must contain a number higher or equal to 1."));
            }

            if (productDTO.getIniValue() < 1) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error",
                                "The iniValue field must contain a number higher or equal to 1."));
            }

            Optional<Product> existingProduct = productService.findByIdOLD(pid);

            if (existingProduct.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Product not found"));
            }

            try {
                product.setName(productDTO.getName());
                product.setDescription(productDTO.getDescription());
                product.setIniValue(productDTO.getIniValue());

                productService.save(product);

                ProductDTO responseDTO = ProductMapper.INSTANCE.toDTO(product);

                return ResponseEntity.ok(responseDTO);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error updating the product: " + e.getMessage()));
            }
        }

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
        if (!userService.getActiveById(loggedInUser.getId())) {
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
        if (!userService.getActiveById(loggedInUser.getId())) {
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

        Optional<UserModel> user = userService.findByNameOLD(principal.getName());
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
                if (userService.getUserTypeById(user.get().getId()).equals("Registered User") && user.get().getId() == updatedUserDTO.getId()) {
                    PublicUserDTO userUpdated = userService.replaceUser(updatedUserDTO);
                    return ResponseEntity.ok(userUpdated);
                }
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