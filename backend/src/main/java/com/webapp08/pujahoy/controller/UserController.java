package com.webapp08.pujahoy.controller;

import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;

import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.UserService;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.service.RatingService;
import com.webapp08.pujahoy.service.TransactionService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private TransactionService transactionService;

    @ModelAttribute // Responsible for adding the attributes to the model in every request
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping() // Responsible for verifying that the parameters passed are valid and, if so, redirecting to the profile page of the user
    public String profileIndex(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String username = principal.getName(); 
            Optional<UserModel> user = userService.findByName(username); 
            if (user.get().determineUserType().equals("Administrator")){
                model.addAttribute("text", " you dont have a profile");
                model.addAttribute("url", "/");
                return "pageError";
            }
            if (user.isPresent()) {
                model.addAttribute("userInfo", user.get());
                model.addAttribute("id", user.get().getId());
                model.addAttribute("name", user.get().getName());
                model.addAttribute("visibleName", user.get().getVisibleName());
                model.addAttribute("reputation", user.get().getReputation());
                model.addAttribute("zipCode", user.get().getZipCode());
                model.addAttribute("contact", user.get().getContact());
                model.addAttribute("description", user.get().getDescription());
                model.addAttribute("profilePic", user.get().getProfilePic());
                model.addAttribute("admin", false);
                model.addAttribute("owner", true);
                if (!user.get().isActive()) { 
                    model.addAttribute("banned", true);
                    model.addAttribute("registered", false);
                } else {
                    model.addAttribute("banned", false);
                    model.addAttribute("registered", true);
                }
                return "profile";
            } else {
                model.addAttribute("text", " user not found");
                model.addAttribute("url", "/");
            }
        }
        model.addAttribute("text", " you must be logged in");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/{id}") // Responsible for verifying that the parameters passed are valid and, if so, redirecting to the profile page of the user or show the seller's profile
    public String viewOtherProfile(Model model, @PathVariable long id, HttpServletRequest request, HttpSession session) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            Optional<UserModel> seller = userService.findByProducts(product.get());
            if (seller.isPresent()) {
                Principal principal = request.getUserPrincipal();
                Optional<UserModel> user;
                String userType;
                if (principal != null) { 
                    String username = principal.getName();
                    user = userService.findByName(username); 
                    userType = user.get().determineUserType();
                    if (user.get().getId() == seller.get().getId()) { 
                        return "redirect:/user";
                    }
                } else {
                    user = null;
                    userType = "";
                }
                model.addAttribute("owner", false);
                model.addAttribute("product", product.get());
                int after = (int) session.getAttribute("after");
                if (after == 3){
                    model.addAttribute("win", true);
                } else {
                    model.addAttribute("win", false);
                }
                model.addAttribute("registered", false);
                model.addAttribute("banned", false);
                model.addAttribute("userInfo", seller.get());
                model.addAttribute("id", seller.get().getId());
                model.addAttribute("name", seller.get().getName());
                model.addAttribute("visibleName", seller.get().getVisibleName());
                model.addAttribute("reputation", seller.get().getReputation());
                model.addAttribute("contact", seller.get().getContact());
                model.addAttribute("description", seller.get().getDescription());
                if (userType.equals("Administrator")) {
                    model.addAttribute("admin", true);
                } else { 
                    model.addAttribute("admin", false);
                }
                if (seller.get().isActive()) {
                    model.addAttribute("banned", false);
                } else {
                    model.addAttribute("banned", true);
                }
                return "profile";
            } else {
                model.addAttribute("text", " seller not found");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("text", " product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @PostMapping()
    public String editProfile(Model model, @RequestParam long id, @RequestParam String contact,
            @RequestParam String description, @RequestParam String zipCode,
            @RequestParam(required = false) MultipartFile profilePic) throws IOException, SQLException { //post in charge of editing a profile form, notice the use of regular expressions

        UserModel user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!zipCode.matches("\\d{5}") || !contact.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        ) {
            return "redirect:/user"; 
        }
        
        user.setContact(contact);
        user.setDescription(description);
        user.setZipCode(Integer.parseInt(zipCode));

        if (profilePic != null && !profilePic.isEmpty()) {
            byte[] photoBytes = profilePic.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            user.setProfilePic(photoBlob);
        }

        userService.save(user); 

        return "redirect:/user"; 
    }

    @PostMapping("/{id}/ban") // Responsible for verifying that the parameters passed are valid and, if so, banning or unbanning the user
    public String bannedUser(Model model, @PathVariable String id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<UserModel> admin = userService.findByName(principal.getName());
            Optional<UserModel> user = userService.findById(Long.parseLong(id));
            String userType = admin.get().determineUserType();
            if (user.isPresent() && userType.equals("Administrator")) {
                Boolean active = user.get().isActive();
                user.get().changeActive();
                userService.save(user.get());
                if (active) {
                    model.addAttribute("text", "User banned. All his products have been finished.");
                    userService.finishProductsForUser(user.get());
                } else {
                    userService.deleteProducts(user.get());
                    model.addAttribute("text", "User unbanned. All his products have been removed.");
                }
                return "bannedProfile";
            } else if (!userType.equals("Administrator")) {
                model.addAttribute("text", " you are not allow to banned users");
                model.addAttribute("url", "/");
            } else {
                model.addAttribute("text", " user not found");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("text", " you must be logged in");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @GetMapping("/product_template") // Used for loading new products to the your products view
    public String seeProducts(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<UserModel> user = userService.findByName(username);

            if (user.isPresent()) {
                Page<Product> products = productService.obtainPaginatedProducts(username, page, size);

                model.addAttribute("products", products); 
                return "product_template";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/seeProducts") // Responsible for showing the your products view
    public String seeProductsIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpSession session) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<UserModel> user = userService.findByName(username);

            if (user.isPresent()) {
                if (!user.get().isActive()){
                    model.addAttribute("text", " You are banned");
                    model.addAttribute("url", "/user");
                    return "pageError";
                }
                Page<Product> products = productService.obtainPaginatedProducts(username, page, size);
                Boolean button = true;
                if (products.isEmpty()) {
                    button = false;
                }
                session.setAttribute("after", 2);
                model.addAttribute("button", button);
                model.addAttribute("products", products); 
                return "YourProducts";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/product_template_buys") // Used for loading new products to the your winning bids view
    public String seeProductsBuy(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<UserModel> user = userService.findByName(username); 

            if (user.isPresent()) {
                Page<Product> products = productService.obtainProductsBuyed(username, page, size);

                model.addAttribute("products", products); 
                return "product_template";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    // Responsible for showing the your winning bids view
    @GetMapping("/seeBuys")
    public String seeProductsBuyIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpSession session) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            
            String username = principal.getName(); 
            Optional<UserModel> user = userService.findByName(username);

            if (user.isPresent()) {
                if (!user.get().isActive()){
                    model.addAttribute("text", " You are banned");
                    model.addAttribute("url", "/user");
                    return "pageError";
                }
                Page<Product> products = productService.obtainProductsBuyed(username, page, size);
                Boolean button = true;
                if (products.isEmpty()) {
                    button = false;
                }
                session.setAttribute("after", 3);
                model.addAttribute("button", button);
                model.addAttribute("products", products); 
                return "YourWinningBids";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/newProduct")
    public String newProduct(Model model, HttpSession session, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null && userService.findByName(principal.getName()).get().isActive()) {
            session.setAttribute("after", 1);
            return "newAuction";
        } else {
            model.addAttribute("text", " You are banned");
            model.addAttribute("url", "/user");
            return "pageError";
        }
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(HttpSession session, @PathVariable long id, Model model, HttpServletRequest request) {
        session.setAttribute("after", 1);
        Optional<Product> oldProd = productService.findById(id);
        Principal principal = request.getUserPrincipal();
        Optional<UserModel> user = userService.findByName(principal.getName());
        if (!(oldProd.get().getSeller().getId() == user.get().getId() || user.get().determineUserType().equals("Administrator"))) {
            model.addAttribute("text", " This product is not yours");
            model.addAttribute("url", "/");
            return "pageError";
        }
        if (!userService.findByName(principal.getName()).get().isActive()){
            model.addAttribute("text", " You are banned");
            model.addAttribute("url", "/");
            return "pageError";
        }
        if (!oldProd.isPresent()) {
            return "pageError";
        }

        Product product = oldProd.get();

        if (product.getOffers().isEmpty()){
            model.addAttribute("product", product);
            return "editAuction";
        } else {
            model.addAttribute("text", " You cannot edit a product if a user placed a bid");
            model.addAttribute("url", "/product/"+ product.getId());
            return "pageError";
        }
        
    }

    @PostMapping("/submit_edit/{id}")
    public String edit(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("iniValue") double prize,
        @RequestParam("image") MultipartFile imageFile,
        HttpServletRequest request,
        Model model, @PathVariable long id) {

        Principal principal = request.getUserPrincipal();
        Optional<Product> oldProd = productService.findById(id);
        
        if (oldProd.isEmpty()) {
            model.addAttribute("text", " Product not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Product oldP = oldProd.get();
        
        if (principal == null) {
            model.addAttribute("text", "User not authenticated");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Optional<UserModel> user = userService.findByName(principal.getName());

        if (user.isEmpty()) {
            model.addAttribute("text", "User not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        if (!user.get().isActive()){
            model.addAttribute("text", " You are banned");
            model.addAttribute("url", "/");
            return "pageError";
        }

        try {
            
            oldP.setName(name);
            oldP.setDescription(description);
            oldP.setIniValue(prize); 

            if (!imageFile.isEmpty()) {
                Blob image = BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize());
                oldP.setImage(image);
            }

            productService.save(oldP); 

            model.addAttribute("product", oldP);
            return "redirect:/product/" + oldP.getId();

        } catch (Exception e) {
            model.addAttribute("text", "Error processing the product: " + e.getMessage());
            model.addAttribute("url", "/");
            return "pageError";
        }
    }

    // Used for creating new listings
    @PostMapping("/submit_auction")
    public String publishProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("iniValue") double prize,
            @RequestParam("time") int time,
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request,
            Model model) {

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            model.addAttribute("text", " User not authenticated");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Optional<UserModel> user = userService.findByName(principal.getName());

        if (user.isEmpty()) {
            model.addAttribute("text", " User not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        try {
            Date iniHour = new Date(System.currentTimeMillis());
            Date endHour = new Date(iniHour.getTime() + (long) time * 24 * 60 * 60 * 1000);

            Blob image = null;
            if (!imageFile.isEmpty()) {
                image = BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize());
            }

            Product product = new Product(name, description, prize, iniHour, endHour, "In progress", image,
                    user.get());
            
            
            product.setImgURL("/product/" + product.getId() +"/image");
            productService.save(product);

            model.addAttribute("product", product);
            return "redirect:/product/" + product.getId();

        } catch (Exception e) {
            model.addAttribute("text", " Error processing the product: " + e.getMessage());
            model.addAttribute("url", "/");
            return "pageError";
        }
    }

    @GetMapping("/{id}/rate") // Responsible for verifying that the parameters passed are valid and, if so, redirecting to the rating page
    public String gotoRate(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                model.addAttribute("text", "you must be logged in");
                model.addAttribute("url", "/product/" + id);
                return "pageError";
            }
            Optional<Transaction> trans = transactionService.findByProduct(product.get());
            if (trans.isEmpty()) {
                model.addAttribute("text", "this product has not been sold");
                model.addAttribute("url", "/product/" + id);
                return "pageError";
            }
            Optional<UserModel> user = userService.findById(trans.get().getBuyer().getId());
            Optional<UserModel> user1 = userService.findByName(principal.getName());
            if (user.isPresent() && user1.isPresent()) {
                if (user.get().determineUserType().equals("Registered User")
                        && user1.get().getId().equals(user.get().getId())) {
                    model.addAttribute("id", id);
                    return "ratingProduct";
                } else {
                    model.addAttribute("text", "this product is not yours");
                    model.addAttribute("url", "/product/" + id);
                }
            } else {
                model.addAttribute("text", "buyer not exist");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("text", "product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @PostMapping("/{id}/rated") // Responsible for verifying that the parameters passed are valid and, if so, saving them in the database
    public String rateProduct(Model model, @PathVariable long id, @RequestParam int rating) {
        if (rating < 1 || rating > 5) {
            model.addAttribute("text", " the rated must be between 1 and 5");
            model.addAttribute("url", "/producto/" + id + "/rate");
            return "pageError";
        }
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            Optional<Rating> existingVal = ratingService.findByProduct(product.get());
            if (existingVal.isPresent()) {
                model.addAttribute("text", " This product has already been rated");
                model.addAttribute("url", "/product/" + id);
                return "pageError";
            }
            Rating val = new Rating(product.get().getSeller(), product.get(), rating);
            ratingService.save(val);
            userService.updateRating(product.get().getSeller());
            model.addAttribute("id", product.get().getId());
            return "productRated";
        } else {
            model.addAttribute("text", " product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @GetMapping("/{id}/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable long id) {

        Optional<UserModel> user = userService.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Blob profilePic = user.get().getProfilePic();

        try {
            byte[] picBytes = profilePic.getBytes(1, (int) profilePic.length());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(picBytes, headers, HttpStatus.OK);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
