package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.ProductMapper;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.RatingDTO;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.RatingService;
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
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private RatingService ratingService;

    @GetMapping("")
    public ResponseEntity<PublicUserDTO> me(HttpServletRequest request) { //Get his own details
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
			Optional<UserModel> user = userService.findByName(principal.getName());
            if (user.isPresent()) {
                URI location = fromCurrentRequest().path("/").buildAndExpand(user.get().getId()).toUri();
                return ResponseEntity.created(location).body(userService.findUser(user.get().getId()));
            }
        }
        return ResponseEntity.badRequest().build(); //The user is not logged in or the user is not found
    }
    
    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id) { //Get user by id
        return userService.findUser(id);
    }

    @PostMapping("/{id}/product")
        public ResponseEntity<?> publishProduct(@RequestBody ProductDTO productDTO, HttpServletRequest request, @PathVariable Long id) {
        
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        if (!user.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You are not allowed to publish products for another user"));
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

        if (productDTO.getDuration() < 1){
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "The duration field must contain a number higher or equal to 1."));
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
                    user.get()
            );

            
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
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @PathVariable Long pid, @RequestBody ProductDTO productDTO, HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        Optional<Product> optionalProduct = productService.findById(pid);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Product product = optionalProduct.get();
        UserModel loggedInUser = user.get();


        if (!(product.getSeller().getId().equals(loggedInUser.getId()) || loggedInUser.determineUserType().equals("Administrator"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You do not have permission to modify this product"));
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

        if (productDTO.getDuration() < 1){
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "The duration field must contain a number higher or equal to 1."));
        }

        Optional<Product> existingProduct = productService.findById(pid);

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
    
    @GetMapping("/{id}/products")
    public ResponseEntity<?> getUserProducts(@PathVariable Long id,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        UserModel loggedInUser = user.get();
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
                                                
        Principal principal = request.getUserPrincipal();                                        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }

        UserModel loggedInUser = user.get();
        if (!loggedInUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductBasicDTO> boughtProducts = productService.findBoughtProductsByUser(pageable, id);

        return ResponseEntity.ok(boughtProducts);
    }

    
    @GetMapping("/{id}/image")//Get user image
    public ResponseEntity<Object> getPostImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = userService.getPostImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

    @PutMapping("") //Update user
	public ResponseEntity<PublicUserDTO> replacePost(@RequestBody PublicUserDTO updatedUserDTO, HttpServletRequest request) throws SQLException {
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
			Optional<UserModel> user = userService.findByName(principal.getName());
            if (user.isPresent()) {
                if (user.get().determineUserType() == "Administrator") { //Administrator = Banned user
                    Optional<PublicUserDTO> newUser = userService.bannedUser(updatedUserDTO.getId(), updatedUserDTO);
                    if (newUser.isPresent()){
                        return ResponseEntity.ok(userService.findUser(newUser.get().getId()));
                    } else {
                        return ResponseEntity.badRequest().build(); //There is not user authenticated
                    }
                } else { //Registered User = edit own profile
                    //userService.replaceUser(updatedUserDTO.getId(), updatedUserDTO);
                }
            }
        } else {
            return ResponseEntity.badRequest().build(); //There is not user authenticated
        }        
        return null;
	}

    public void updateRating(UserModel user) { // Responsible for updating the reputation of a user
        List<Rating> ratings = ratingService.findAllBySeller(user);
        if (ratings.isEmpty()) {
            return; 
        }
        int amount = 0;
        for (Rating val : ratings) {
            amount += val.getRating();
        }
        double mean = (double) amount / ratings.size();

        user.setReputation(mean);
        userService.save(user);
    }

    @PostMapping("/{user_id}/products/{product_id}/ratings") //Rate user
	public ResponseEntity<RatingDTO> rateProduct(@PathVariable long user_id, @PathVariable long product_id, @RequestBody RatingDTO ratingDTO, HttpServletRequest request) {
        //Check if the user can rate this product
        Principal principal = request.getUserPrincipal();
        if(principal == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UserModel> user = userService.findByName(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<UserModel> seller = userService.findById(user_id);
        Optional<Product> product = productService.findById(product_id);

        if (seller.isPresent() && product.isPresent() && seller.get().getId() == product.get().getSeller().getId() && ratingDTO.getRating() >= 1 && ratingDTO.getRating() <= 5) { //User is the seller of the product
            Optional<Rating> test = ratingService.findByProduct(product.get());
            if (test.isPresent()) {
                return ResponseEntity.badRequest().build(); //the product is already rated
            }
            Optional<Transaction> trans = transactionService.findByProduct(product.get());
            if(!trans.isPresent() && user.get() != trans.get().getBuyer()) {
                return ResponseEntity.badRequest().build(); //the product is not sold or the user is not the buyer
            }
            RatingDTO newRatingDTO = ratingService.createRating(ratingDTO.getRating(), seller.get(), product.get());
            this.updateRating(seller.get());
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(newRatingDTO.getId()).toUri();

            return ResponseEntity.created(location).body(newRatingDTO);
        } else { //User is not the seller of the product
            return ResponseEntity.badRequest().build();
        }
	}
}