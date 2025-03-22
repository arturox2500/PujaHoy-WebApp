package com.webapp08.pujahoy.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.Date;
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

import com.webapp08.pujahoy.dto.OfferDTO;
import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.OfferService;
import com.webapp08.pujahoy.service.ProductService;
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
    private OfferService offerService;

    
    @GetMapping("/{id_product}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable long id_product) {
        Optional<Product> product = productService.findById(id_product);
        if (product.isPresent()) {
            ProductDTO prod=productService.findProduct(id_product);
            return ResponseEntity.ok(prod);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public Page<ProductBasicDTO> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();
        if(principal != null) {
			Optional<UserModel> useraux = userService.findByName(principal.getName());
            if (useraux.isPresent()) {
                UserModel user= useraux.get();
                
                //isAdmin
                if("Administrator".equalsIgnoreCase(user.determineUserType())){
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
    public ResponseEntity<OfferDTO> PlaceBid(@PathVariable long id_product, @RequestBody OfferDTO offerDTO,HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if(principal == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<UserModel> user = userService.findByName(principal.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<UserModel> bidder = userService.findById(user.get().getId());
        Optional<Product> product = productService.findById(id_product);

        //User Comprobation
        if(bidder.isPresent()){
            if("Administrator".equalsIgnoreCase(bidder.get().determineUserType())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if(!bidder.get().isActive()){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.badRequest().build();
        }

        //Product Comprobation
        if (product.isPresent()) {
            if(!product.get().isActive()){
                return ResponseEntity.badRequest().build();
            }
        }else{
            return ResponseEntity.badRequest().build();
        }

        // bidder not the seller
        if(bidder.get().getId()==product.get().getSeller().getId()){
            return ResponseEntity.badRequest().build();
        }


        //Get last bid
        Offer lastOffer = offerService.findLastOfferByProduct(id_product);
        //Set min cost
        double actualPrice;
        if (lastOffer != null) {
            actualPrice = lastOffer.getCost();
        } else {
            actualPrice = product.get().getIniValue() - 1;
        }
        if(offerDTO.cost()>actualPrice){
            long currentTime = System.currentTimeMillis();
            Date currentDate = new Date(currentTime); 
            Offer newOffer=new Offer(bidder.get(),product.get(),offerDTO.cost(),currentDate);

            product.get().getOffers().add(newOffer); 
            offerService.save(newOffer);
            productService.save(product.get());
            OfferDTO offer=offerService.toDTO(newOffer);
            
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(offer.id()).toUri();

            return ResponseEntity.created(location).body(offer);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id_product}/offers")
    public ResponseEntity<List<OfferDTO>> getOffers(@PathVariable long id_product) {
        Optional<Product> product = productService.findById(id_product);
        
        if (product.isPresent()) {
            List<OfferDTO> offerDTOs = offerService.toDTOs(product.get().getOffers());
            return ResponseEntity.ok(offerDTOs); 
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
    

    


    @DeleteMapping("/{id_product}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id_product, HttpServletRequest request) {
        Optional<Product> product = productService.findById(id_product);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserModel> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserModel loggedInUser = user.get();
        Product existingProduct = product.get();

        // Check if is a User and he is the owner
        if ("Registered User".equalsIgnoreCase(loggedInUser.determineUserType()) && !existingProduct.getSeller().getId().equals(loggedInUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        productService.deleteById(id_product);
        return ResponseEntity.noContent().build();
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

        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        if (!optionalProduct.get().getOffers().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You cannot edit a product if a user placed a bid"));
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());

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

        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User not authenticated"));
        }

        Optional<UserModel> user = userService.findByName(principal.getName());

        Product product = optionalProduct.get();
        UserModel loggedInUser = user.get();

        if (!product.getSeller().getId().equals(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You do not have permission to upload an image for this product"));
        }

        if (!loggedInUser.isActive()) {
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

    // @GetMapping("/transactions/{id_product}") 
// public ResponseEntity<TransactionDTO> getTransaction(  
//         @PathVariable long id_product,  
//         HttpServletRequest request) {  

//     Principal principal = request.getUserPrincipal(); 
//     if (principal == null) { 
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED) 
//                 .body(null); 
//     }

//     Optional<UserModel> user = userService.findByName(principal.getName()); 
//     if (user.isEmpty()) { 
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); 
//     }

//     Optional<Product> product = productService.findById(id_product); 
//     if (product.isEmpty() || !product.get().isActive()) { 
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
//     }

//     Optional<Transaction> transaction = transactionService.findByProductId(id_product); 
//     if (transaction.isEmpty()) { 
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
//     }

//     UserModel loggedInUser = user.get(); 
//     if (!transaction.get().getBuyer().getId().equals(loggedInUser.getId()) &&  
//         !product.get().getSeller().getId().equals(loggedInUser.getId())) { 
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); 
//     }

//     TransactionDTO transactionDTO = transactionService.toDTO(transaction.get()); 
//     return ResponseEntity.ok(transactionDTO); 
// }


    
}
