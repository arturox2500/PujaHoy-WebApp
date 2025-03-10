package com.webapp08.pujahoy.controller;

import java.security.Principal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;

import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.service.OfferService;
import com.webapp08.pujahoy.service.ProductService;
import com.webapp08.pujahoy.service.TransactionService;
import com.webapp08.pujahoy.service.UserService;
import com.webapp08.pujahoy.service.RatingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OfferService offerService;

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

    @GetMapping("/")
    public String productList(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Product> product;
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String username = principal.getName();
            Optional<UserModel> userOpt = userService.findByName(username);

            if (!userOpt.isPresent()) {
                model.addAttribute("text", " User not found");
                model.addAttribute("url", "/");
                return "pageError";
            }

            UserModel user = userOpt.get();
            if("Administrator".equalsIgnoreCase(user.determineUserType())){
                product = productService.obtainAllProductOrdersByReputation(page, size);
            }else{
                product = productService.obtainAllProductOrdersInProgressByReputation(page, size);
            }
        }else{
            product = productService.obtainAllProductOrdersInProgressByReputation(page, size);
        }


        Boolean button = true;
        if (product.isEmpty()) {
            button = false;
        }

        model.addAttribute("button", button);
        model.addAttribute("products", product);

        return "index";
    }

    @GetMapping("/product_template_index")
    public String seeProducts(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpSession session) {

                Page<Product> product;
                Principal principal = request.getUserPrincipal();
                if (principal != null) {
                    String username = principal.getName();
                    Optional<UserModel> userOpt = userService.findByName(username);
        
                    if (!userOpt.isPresent()) {
                        model.addAttribute("text", " User not found");
                        model.addAttribute("url", "/");
                        return "pageError";
                    }
        
                    UserModel user = userOpt.get();
                    if("Administrator".equalsIgnoreCase(user.determineUserType())){
                        product = productService.obtainAllProductOrdersByReputation(page, size);
                    }else{
                        product = productService.obtainAllProductOrdersInProgressByReputation(page, size);
                    }
                }else{
                    product = productService.obtainAllProductOrdersInProgressByReputation(page, size);
                }
                session.setAttribute("after", 1);
        model.addAttribute("products", product);
        return "product_template";

    }

    @PostMapping("/product/{id_product}/delete")
    public String deleteProduct(Model model, @PathVariable long id_product) {
        Optional<Product> product = productService.findById(id_product);

        if (product.isPresent()) {
            if (!product.get().getOffers().isEmpty()) {
                for (Offer oferta : product.get().getOffers()) {
                    offerService.deleteById(oferta.getId());
                }
            }
            Optional<Transaction> trans = transactionService.findByProduct(product.get());
            if (trans.isPresent()) {
                transactionService.deleteById(trans.get().getId());
            }
            Optional<Rating> rate = ratingService.findByProduct(product.get());
            if (rate.isPresent()) {
                rate.get().setProduct(null);
            }
            productService.deleteById(id_product);
            return "redirect:/";
        } else {
            model.addAttribute("text", " Error deleting product");
            model.addAttribute("url", "/");
            return "pageError";
        }
    }

    @GetMapping("/product/{id_product}")
    public String showProduct(@PathVariable long id_product, Model model, HttpServletRequest request, HttpSession session) {
        Optional<Product> productOpt = productService.findById(id_product);
        if (!productOpt.isPresent()) {
            model.addAttribute("text", " Product not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Product product = productOpt.get();
        model.addAttribute("product", product);

        long actualTime = System.currentTimeMillis();
        if (product.getEndHour().getTime() <= actualTime && product.getState().equals("In proccess")) {
            product.setState("Finished");

            List<Offer> offers = product.getOffers();
            if (!offers.isEmpty()) {
                Offer lastOffer = offers.get(offers.size() - 1);
                Transaction transaccion = new Transaction(product, product.getSeller(), lastOffer.getUser(),
                        lastOffer.getCost());
                transactionService.save(transaccion);
            }
        }

        productService.save(product);

        List<Offer> offers = product.getOffers();
        double[] costs;
        int numOffers = offers.size();
        if (numOffers > 0) {
            costs = new double[numOffers];
            for (int i = 0; i < numOffers; i++) {
                costs[i] = offers.get(i).getCost();
            }
        } else {
            costs = new double[0];
        }

        model.addAttribute("costs", Arrays.toString(costs));

        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String username = principal.getName();
            Optional<UserModel> userOpt = userService.findByName(username);

            if (!userOpt.isPresent()) {
                model.addAttribute("text", " User not found");
                model.addAttribute("url", "/");
                return "pageError";
            }

            UserModel user = userOpt.get();
            model.addAttribute("isSeller", product.getSeller().equals(user));

            model.addAttribute("zipCode", product.getSeller().getZipCode());

            if (user != null) {
                boolean esAdmin = "Administrator".equalsIgnoreCase(user.determineUserType());
                model.addAttribute("admin", esAdmin);
                model.addAttribute("authenticated_user", true);


                if (product.getState().equals("Finished") || product.getState().equals("Delivered")) {
                    model.addAttribute("Finished", true);

                    if (!offers.isEmpty()) {
                        Offer lastOffer = offers.get(offers.size() - 1);
                        model.addAttribute("Winner", lastOffer.getUser().equals(user));
                    }
                } else {
                    model.addAttribute("Finished", false);
                }
            } else {
                model.addAttribute("admin", false);
                model.addAttribute("authenticated_user", false);
            }
            if (session.getAttribute("after") != null) {
                int after = (int) session.getAttribute("after");
                if (after == 2 || after == 3) {
                    if (after == 2){
                        model.addAttribute("a2", true);
                    } else {
                        model.addAttribute("a2", false);
                    }
                    model.addAttribute("after", true);
                } else {
                    model.addAttribute("after", false);
                }
            }
            Optional<Transaction> trans = transactionService.findByProduct(product);
            if (trans.isPresent() && trans.get().getBuyer().getName().equals(user.getName())
                    && !product.getState().equals("Delivered")) {
                model.addAttribute("buyer", true);
            } else {
                model.addAttribute("buyer", false);
            }
        }

        if (!offers.isEmpty()) {
            Offer lastOffer = offers.get(offers.size() - 1);
            model.addAttribute("Winning bid", lastOffer.getCost());
            model.addAttribute("Winner bidder", lastOffer.getUser().getName());
        } else {
            model.addAttribute("Winning bid", "-");
            model.addAttribute("Winner bidder", "-");
        }

        return "product";
    }

    @PostMapping("/product/{id_product}/place-bid")
    public String placeBid(@PathVariable long id_product, @RequestParam double bid_amount, HttpServletRequest request,
            Model model) {

        Optional<Product> productOpt = productService.findById(id_product);

        if (!productOpt.isPresent()) {
            model.addAttribute("text", " Product not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Product product = productOpt.get();

        Principal principal = request.getUserPrincipal();

        String username = principal.getName();
        Optional<UserModel> userOpt = userService.findByName(username);

        if (!userOpt.isPresent()) {
            model.addAttribute("text", " User not found.");
            model.addAttribute("url", "/");
            return "pageError";
        }

        UserModel user = userOpt.get();

        Offer lastOffer = offerService.findLastOfferByProduct(id_product);

        double actualPrice;
        if (lastOffer != null) {
            actualPrice = lastOffer.getCost();
        } else {
            actualPrice = product.getIniValue() - 1;
        }

        if (bid_amount <= actualPrice) {
            model.addAttribute("text", " The bid have to be higher than the current price.");
            model.addAttribute("url", "/product/" + id_product);
            return "pageError";
        }

        long actualTime = System.currentTimeMillis();
        Date actualDate = new Date(actualTime);

        Offer newOffer = new Offer(user, product, bid_amount, actualDate);

        product.getOffers().add(newOffer); 

        offerService.save(newOffer);
        productService.save(product);

        model.addAttribute("url", "/product/" + id_product);

        return "placeBidOk";
    }

    // Used for downloading images from the BBDD
    @GetMapping("/product/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        Optional<Product> op = productService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {

            Blob image = op.get().getImage();
            Resource file = new InputStreamResource(image.getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(image.length()).body(file);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/product/{id_product}/finish")
    public String finishProduct(Model model, @PathVariable long id_product) {
        Optional<Product> product = productService.findById(id_product);

        if (product.isPresent()) {
            product.get().setState("Delivered");
            productService.save(product.get());
            return "redirect:/product/" + id_product;
        } else {
            model.addAttribute("text", " Error deleting product");
            model.addAttribute("url", "/product/" + id_product);
            return "pageError";
        }
    }

}
