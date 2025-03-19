package com.webapp08.pujahoy.controller;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.RatingDTO;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.RatingService;
import com.webapp08.pujahoy.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import java.net.URI;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{id}")
    public PublicUserDTO getUserById(@PathVariable Long id) { //Get user by id
        return userService.findUser(id);
    }

    @GetMapping("/{id}/products")
    public Page<ProductBasicDTO> getUserProducts(@PathVariable Long id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.findProductsByUser(pageable, id);
    }


    @GetMapping("/{id}/boughtProducts")
    public Page<ProductBasicDTO> getBoughtProducts(@PathVariable Long id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.findBoughtProductsByUser(pageable, id);
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
	public PublicUserDTO replacePost(@RequestBody PublicUserDTO updatedUserDTO) throws SQLException {
        //FALTA COMPROBAR QUE DATOS SE CAMBIAN Y QUIEN HACE EL CAMBIO
		return userService.replaceUser(updatedUserDTO.getId(), updatedUserDTO);
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
	public ResponseEntity<RatingDTO> rateProduct(@PathVariable long user_id, @PathVariable long product_id, @RequestBody int rating) {

        Optional<UserModel> user = userService.findById(user_id);
        Optional<Product> product = productService.findById(product_id);

        if (user.isPresent() && product.isPresent() && user.get().getId() == product.get().getSeller().getId() && rating >= 1 && rating <= 5) { //User is the seller of the product
            Optional<Rating> test = ratingService.findByProduct(product.get());
            if (test.isPresent()) {
                return ResponseEntity.badRequest().build(); //the product is already rated
            }
            RatingDTO RatingDTO = ratingService.createRating(rating, user.get(), product.get());
            this.updateRating(user.get());
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(RatingDTO.getId()).toUri();

            return ResponseEntity.created(location).body(RatingDTO);
        } else { //User is not the seller of the product
            return ResponseEntity.badRequest().build();
        }
	}
}