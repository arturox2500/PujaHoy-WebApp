package com.webapp08.pujahoy.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.service.ProductService;


@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id_product}")
    public ProductDTO getProduct(@PathVariable long id_product) {
        return productService.findProduct(id_product);
    }

    @GetMapping
    public Page<ProductBasicDTO> getProducts(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.findProducts(pageable);
    }

    


    @DeleteMapping("/{id_product}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id_product) {
        Optional<Product> product = productService.findById(id_product);
        if (product.isPresent()) {
            productService.deleteById(id_product);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPostImage(@PathVariable long id) throws SQLException {
        Resource postImage = productService.getPostImage(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") 
                .body(postImage);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> updatePostImage(@PathVariable long id, @RequestParam("image") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "No image uploaded"));
        }

        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }

        try {
            productService.savePostImage(id, imageFile);

            return ResponseEntity.ok(Collections.singletonMap("message", "ok"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error updating image: " + e.getMessage()));
        }
    }

    

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadPostImage(@PathVariable long id, @RequestParam("image") MultipartFile imageFile) {
        try {
            productService.savePostImage(id, imageFile);
            return ResponseEntity.ok("ok");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
