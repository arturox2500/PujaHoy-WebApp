package com.webapp08.pujahoy.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import com.webapp08.pujahoy.dto.OfferBasicDTO;
import com.webapp08.pujahoy.dto.OfferDTO;
import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.RatingDTO;
import com.webapp08.pujahoy.dto.TransactionDTO;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.RatingService;
import com.webapp08.pujahoy.service.TransactionService;
import com.webapp08.pujahoy.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;



@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RatingService ratingService;

    
    @GetMapping("/{id_product}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable long id_product) {
    Optional<ProductDTO> product = productService.findById(id_product);

    if (product.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    ProductDTO existingProduct = product.get();

    // Si el producto ya no está activo, cambiar su estado a "Finished"
    if (!existingProduct.isActive()) {
        productService.setStateFinishedProduct(id_product);

        // Volvemos a obtener el producto actualizado después del cambio de estado
        existingProduct = productService.findById(id_product).orElse(existingProduct);
    }

    // Verificar si necesita una transacción
    Optional<TransactionDTO> trans = transactionService.findByProduct(existingProduct.getId());
    if (!existingProduct.getState().equals("In progress") && trans.isEmpty() && userService.getActiveById(existingProduct.getSeller().getId())) {
        transactionService.createTransaction(existingProduct.getId());
    }

    return ResponseEntity.ok(existingProduct);
}

    @GetMapping
    public Page<ProductBasicDTO> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
			Optional<PublicUserDTO> useraux = userService.findByName(principal.getName());
            if (useraux.isPresent()) {
                PublicUserDTO user= useraux.get();
                
                //isAdmin
                if("Administrator".equalsIgnoreCase(userService.getTypeById(user.getId()))){
                    return productService.obtainAllProductOrdersByReputationDTO(page,size);
                }else{//Registered
                    return productService.obtainAllProductOrdersInProgressByReputationDTO(page,size); 
                }
            }
        }
        //Not registered
        return productService.obtainAllProductOrdersInProgressByReputationDTO(page,size);
        
    }
    @PostMapping("/{id_product}/offers")
    public ResponseEntity<?> PlaceBid(@PathVariable long id_product, @RequestBody OfferDTO offerDTO,HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<PublicUserDTO> bidder = userService.findById(user.get().getId());
        Optional<ProductDTO> product = productService.findById(id_product);

        //User Comprobation
        if(bidder.isPresent()){
            if("Administrator".equalsIgnoreCase(userService.getTypeById(bidder.get().getId()))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(!userService.getActiveById(bidder.get().getId())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(bidder.get().getId().equals(product.get().getSeller().getId())){
                return ResponseEntity.badRequest().body("The seller cannot bid");

            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //Product Comprobation
        if (product.isPresent()) {
            if(!product.get().isActive()){
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Product is Finished"));
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // bidder not the seller
        if(bidder.get().getId()==product.get().getSeller().getId()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        //Make the bid
        OfferDTO offer =productService.PlaceABid(product.get(), offerDTO.cost(), bidder.get());
        
        if(offer != null){
            
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(offer.id()).toUri();

            return ResponseEntity.created(location).body(offer);
        }else{
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Bid is to low")); //Bid is too low
        }
    }

    @GetMapping("/{id_product}/offers")
    public ResponseEntity<List<OfferBasicDTO>> getOffers(@PathVariable long id_product) {
        Optional<ProductDTO> product = productService.findById(id_product);
        
        if (product.isPresent()) {
            List<OfferBasicDTO> offerDTOs = product.get().getOffers();
            return ResponseEntity.ok(offerDTOs); 
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }

    @PostMapping("")
    public ResponseEntity<?> publishProduct(@RequestBody ProductDTO productDTO, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Optional<PublicUserDTO> user = userService.findByName(principal.getName());

        PublicUserDTO loggedInUser = user.get();
        if (!userService.getActiveById(loggedInUser.getId())) {
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
            ProductDTO responseDTO = productService.createProduct(productDTO, user.get());
            URI location = fromCurrentRequest().path("/products/{id}").buildAndExpand(responseDTO.getId()).toUri();

            return ResponseEntity.created(location).body(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error processing the product: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{pid}")
    public ResponseEntity<?> updateProduct(@PathVariable Long pid,
            @RequestBody ProductDTO productDTO, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
        Optional<ProductDTO> optionalProduct = productService.findById(pid);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        ProductDTO product = optionalProduct.get();
        PublicUserDTO loggedInUser = user.get();

        if (!userService.getActiveById(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }
        Optional<ProductDTO> optionalProduct2 = productService.findById(pid);
        Optional<TransactionDTO> trans = transactionService.findByProduct(optionalProduct2.get().getId());

        if (trans.isPresent()) {
            if (trans.get().buyer().id().equals(loggedInUser.getId())) {
                if (productDTO.getName() == null || productDTO.getName().trim().isEmpty() ||
                        productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty() ||
                        productDTO.getDuration() == null || productDTO.getIniValue() == null) {
                    if (productDTO.getState().equals("Delivered")) {
                        Optional<ProductDTO> prod2= productService.setStateDeliveredProduct(pid);
                        if (prod2.isPresent()){
                            return ResponseEntity.ok(prod2.get());
                        }
                        return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "Error"));
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
                    || userService.getUserTypeById(loggedInUser.getId()).equals("Administrator"))) {
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
            try {
                product.setName(productDTO.getName());
                product.setDescription(productDTO.getDescription());
                product.setIniValue(productDTO.getIniValue());
                productService.save(product);

                return ResponseEntity.ok(product);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error updating the product: " + e.getMessage()));
            }
        }

    }
    
    @DeleteMapping("/{id_product}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id_product, HttpServletRequest request) {
        Optional<ProductDTO> product = productService.findById(id_product);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO existingProduct = product.get();

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PublicUserDTO loggedInUser = user.get();
        // Check if the product has registered bids

        if(product.get().getState().equals("In progress")){
            if (!existingProduct.getOffers().isEmpty() ) {
                return ResponseEntity.badRequest().body("You cannot delete a product that has bids.");
            }
            
            if (!"Administrator".equalsIgnoreCase(userService.getTypeById(loggedInUser.getId())) &&
                !existingProduct.getSeller().getId().equals(loggedInUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }   

        productService.deleteById(id_product);
        return ResponseEntity.ok(existingProduct);
    }



    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPostImage(@PathVariable long id) throws SQLException {
        Resource postImage = productService.getPostImage(id);
        if (postImage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(postImage);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> updatePostImage(@PathVariable long id, @RequestParam("image") MultipartFile imageFile,  HttpServletRequest request) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "No image uploaded"));
        }
        Optional<ProductDTO> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }
        if (!optionalProduct.get().getOffers().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You cannot edit a product if a user placed a bid"));
        }

        Principal principal = request.getUserPrincipal();
        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        ProductDTO product = optionalProduct.get();
        PublicUserDTO loggedInUser = user.get();

        if (!(product.getSeller().getId().equals(loggedInUser.getId()) || userService.getUserTypeById(loggedInUser.getId()).equals("Administrator"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You do not have permission to modify this product"));
        }
        if (!userService.getActiveById(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }
        try {
            productService.savePostImage(id, imageFile);
            Resource postImage = productService.getPostImage(id);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") 
                .body(postImage);
        } catch (SQLException e) { 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error updating image: " + e.getMessage()));
        }
    }

    

    @PostMapping("/{id}/image")
    public ResponseEntity<?> postImage(@PathVariable long id, 
                                    @RequestParam("image") MultipartFile imageFile,
                                    HttpServletRequest request) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "No image uploaded"));
        }

        Optional<ProductDTO> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        ProductDTO product = optionalProduct.get();
        PublicUserDTO loggedInUser = user.get();

        if (!product.getSeller().getId().equals(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You do not have permission to upload an image for this product"));
        }
        if (!userService.getActiveById(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Banned user"));
        }
        try {
            productService.savePostImage(id, imageFile);
            Resource postImage = productService.getPostImage(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") 
                    .body(postImage);
        } catch (SQLException e) { 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Database error: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error uploading image: " + e.getMessage()));
        }
    }

    @PostMapping("{product_id}/ratings") // Rate user
    public ResponseEntity<?> rateProduct(@PathVariable long product_id,
            @RequestBody RatingDTO rating, HttpServletRequest request) {
        // Check if the user can rate this product
        Principal principal = request.getUserPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be authenticated");
        }
        Optional<PublicUserDTO> user = userService.findByName(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<ProductDTO> product = productService.findById(product_id);
        if (product.isPresent()){
            Optional<?> respuesta = ratingService.rateProduct(rating.getRating(),product.get(),user.get());
            if (respuesta.get() instanceof RatingDTO){
                RatingDTO newRatingDTO = (RatingDTO) respuesta.get();
                URI location = fromCurrentRequest().path("/{id}").buildAndExpand(newRatingDTO.getId()).toUri();
                return ResponseEntity.created(location).body(newRatingDTO);
            } else {
                int value = (int) respuesta.get();
                switch (value) {
                    case 0:
                    return ResponseEntity.badRequest().body(Collections.singletonMap("error", "The rating must be between 1 and 5"));
                    case 1:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
                    case 2:
                    return ResponseEntity.badRequest().body(Collections.singletonMap("error", "You are not the buyer"));
                    case 3:
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "The product is already rated"));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        
    }
    
}