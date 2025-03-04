package com.webapp08.pujahoy.controller;

import java.util.List;
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

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.service.UsuarioService;
import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.service.OfertaService;
import com.webapp08.pujahoy.service.ProductoService;
import com.webapp08.pujahoy.model.Valoracion;
import com.webapp08.pujahoy.service.ValoracionService;
import com.webapp08.pujahoy.service.TransaccionService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/usuario")
public class UserController {

    @Autowired
    private UsuarioService userService;

    @Autowired
    private ProductoService productService;

    @Autowired
    private ValoracionService ratingService;

    @Autowired
    private TransaccionService transactionService;

    @Autowired
    private OfertaService offerService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping() 
    public String profileIndex(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String username = principal.getName(); 
            Optional<Usuario> user = userService.findByNombre(username); 
            if (user.get().determinarTipoUsuario().equals("Administrador")){
                model.addAttribute("texto", " you dont have a profile");
                model.addAttribute("url", "/");
                return "pageError";
            }
            if (user.isPresent()) {
                model.addAttribute("userInfo", user.get());
                model.addAttribute("id", user.get().getId());
                model.addAttribute("name", user.get().getNombre());
                model.addAttribute("visibleName", user.get().getNombreVisible());
                model.addAttribute("reputation", user.get().getReputacion());
                model.addAttribute("zipCode", user.get().getCodigoPostal());
                model.addAttribute("contact", user.get().getContacto());
                model.addAttribute("description", user.get().getDescripcion());
                model.addAttribute("profilePic", user.get().getFotoPerfil());
                model.addAttribute("admin", false);
                if (!user.get().isActivo()) { 
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

    @GetMapping("/{id}") 
    public String viewOtherProfile(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<Producto> product = productService.findById(id);
        if (product.isPresent()) {
            Optional<Usuario> seller = userService.findByProductos(product.get());
            if (seller.isPresent()) {
                Principal principal = request.getUserPrincipal();
                Optional<Usuario> user;
                String tipo;
                if (principal != null) { 
                    String username = principal.getName();
                    user = usuarioService.findByNombre(username); 
                    userType = user.get().determinarTipoUsuario();
                    if (user.get().getId() == seller.get().getId()) { 
                        return "redirect:/usuario";
                    }
                } else {
                    user = null;
                    userType = "";
                }
                model.addAttribute("registered", false);
                model.addAttribute("banned", false);
                model.addAttribute("userInfo", seller.get());
                model.addAttribute("id", seller.get().getId());
                model.addAttribute("name", seller.get().getNombre());
                model.addAttribute("visibleName", seller.get().getNombreVisible());
                model.addAttribute("reputation", seller.get().getReputacion());
                model.addAttribute("contact", seller.get().getContacto());
                model.addAttribute("description", seller.get().getDescripcion());
                if (userType.equals("Administrador")) {
                    model.addAttribute("admin", true);
                } else { 
                    model.addAttribute("admin", false);
                }
                if (vendedor.get().isActivo()) {
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
            @RequestParam(required = false) MultipartFile profilePic) throws IOException, SQLException {

        Usuario user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!ziPCode.matches("\\d{5}") || !contact.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        ) {
            return "redirect:/usuario"; 
        }
        
        user.setContacto(contact);
        user.setDescripcion(description);
        user.setCodigoPostal(Integer.parseInt(zipCode));

        if (profilePic != null && !profilePic.isEmpty()) {
            byte[] photoBytes = profilePic.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            user.setFotoPerfil(photoBlob);
        }

        userService.save(user); 

        return "redirect:/usuario"; 
    }

    private void finishProductsForUser(Usuario user) {
        List<Producto> products = productService.findByVendedor(user);
        for (Producto product : products) {
            if (!product.getOfertas().isEmpty()) {
                for (Oferta offer : product.getOfertas()) {
                    offerService.deleteById(offer.getId());
                }
            }
            product.setEstado("Finalizado");
            productService.save(product);
        }
    }

    private void deleteProducts(Usuario user) {
        List<Producto> products = productService.findByVendedor(user);
        for (Producto product : products) {
            if (!product.getOfertas().isEmpty()) {
                for (Oferta offer : product.getOfertas()) {
                    offerService.deleteById(offer.getId());
                }
            } 
            Optional<Transaccion> trans = transactionService.findByProducto(product);
            if (trans.isPresent()) {
                transactionService.deleteById(trans.get().getId());
            }
            productService.DeleteById(product.getId());
        }
    }

    @PostMapping("/{id}/banear")
    public String bannedUser(Model model, @PathVariable String id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<Usuario> admin = userService.findByNombre(principal.getName());
            Optional<Usuario> user = userService.findById(Long.parseLong(id));
            String userType = admin.get().determinarTipoUsuario();
            if (user.isPresent() && userType.equals("Administrador")) {
                Boolean active = user.get().isActivo();
                user.get().changeActivo();
                userService.save(user.get());
                if (active) {
                    model.addAttribute("text", "User banned. All his products have been finished.");
                    this.finishProductsForUser(user.get());
                } else {
                    this.deleteProducts(user.get());
                    model.addAttribute("text", "User unbanned. All his products have been removed.");
                }
                return "bannedProfile";
            } else if (!userType.equals("Administrador")) {
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

    @GetMapping("/producto_template")
    public String seeProducts(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<Usuario> user = userService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> products = productService.obtenerProductosPaginados(username, page, size);

                model.addAttribute("products", products); 
                return "producto_template";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/verProductos")
    public String seeProductsIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<Usuario> user = userService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> products = productService.obtenerProductosPaginados(username, page, size);
                Boolean button = true;
                if (products.isEmpty()) {
                    button = false;
                }

                model.addAttribute("button", button);
                model.addAttribute("products", products); 
                return "YourProducts";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/producto_template_compras")
    public String seeProductsBuy(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<Usuario> user = user.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> products = productService.obtenerProductosComprados(username, page, size);

                model.addAttribute("products", products); 
                return "producto_template";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/verCompras")
    public String seeProductsBuyIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); 
            Optional<Usuario> user = userService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> products = productService.obtenerProductosComprados(username, page, size);
                Boolean button = true;
                if (productos.isEmpty()) {
                    button = false;
                }

                model.addAttribute("button", button);
                model.addAttribute("products", products); 
                return "YourWinningBids";
            }
        }

        model.addAttribute("text", " You are not authenticated");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/NuevoProducto")
    public String newProduct() {
        return "newAuction";
    }

    @PostMapping("/submit_auction")
    public String publishProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("iniValue") double prize,
            @RequestParam("time") int time,
            @RequestParam("state") String state,
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request,
            Model model) {

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            model.addAttribute("text", " User not authenticated");
            model.addAttribute("url", "/");
            return "pageError";
        }

        Optional<Usuario> user = userService.findByNombre(principal.getName());

        if (user.isEmpty()) {
            model.addAttribute("texto", " User not found");
            model.addAttribute("url", "/");
            return "pageError";
        }

        try {
            Date iniHour = new Date(System.currentTimeMillis());
            Date endHour = new Date(iniHour.getTime() + (long) duracion * 24 * 60 * 60 * 1000);

            Blob image = null;
            if (!imageFile.isEmpty()) {
                image = BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize());
            }

            Producto product = new Producto(name, description, prize, iniHour, endHour, state, image,
                    user.get());
    
            productService.save(product);

            model.addAttribute("product", product);
            return "redirect:/producto/" + product.getId();

        } catch (Exception e) {
            model.addAttribute("text", " Error processing the product: " + e.getMessage());
            model.addAttribute("url", "/");
            return "pageError";
        }
    }

    @GetMapping("/{id}/rate") 
    public String gotoRate(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<Producto> product = productService.findById(id);
        if (product.isPresent()) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                model.addAttribute("text", "you must be logged in");
                model.addAttribute("url", "/producto/" + id);
                return "pageError";
            }
            Optional<Transaccion> trans = transactionService.findByProducto(product.get());
            if (trans.isEmpty()) {
                model.addAttribute("text", "this product has not been sold");
                model.addAttribute("url", "/producto/" + id);
                return "pageError";
            }
            Optional<Usuario> user = userService.findById(trans.get().getComprador().getId());
            Optional<Usuario> user1 = userService.findByNombre(principal.getName());
            if (user.isPresent() && user1.isPresent()) {
                if (user.get().determinarTipoUsuario().equals("Usuario Registrado")
                        && user1.get().getId().equals(user.get().getId())) {
                    model.addAttribute("id", id);
                    return "ratingProduct";
                } else {
                    model.addAttribute("text", "this product is not yours");
                    model.addAttribute("url", "/producto/" + id);
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

    public void updateRating(Usuario user) {
        List<Valoracion> ratings = rating.findAllByVendedor(user);
        if (ratings.isEmpty()) {
            return; 
        }
        int amount = 0;
        for (Valoracion val : ratings) {
            amount += val.getPuntuacion();
        }
        double mean = (double) amount / ratings.size();

        user.setReputacion(mean);
        usuarioService.save(user);
    }

    @PostMapping("/{id}/rated")
    public String rateProduct(Model model, @PathVariable long id, @RequestParam int rating) {
        if (rating < 1 || rating > 5) {
            model.addAttribute("text", " the rated must be between 1 and 5");
            model.addAttribute("url", "/producto/" + id + "/rate");
            return "pageError";
        }
        Optional<Producto> product = productService.findById(id);
        if (product.isPresent()) {
            Optional<Valoracion> existingVal = ratingService.findByProducto(product.get());
            if (existingVal.isPresent()) {
                model.addAttribute("text", " This product has already been rated");
                model.addAttribute("url", "/producto/" + id + "/rate");
                return "pageError";
            }
            Valoracion val = new Valoracion(product.get().getVendedor(), product.get(), rating);
            ratingService.save(val);
            this.updateRating(product.get().getVendedor());
            model.addAttribute("id", product.get().getId());
            return "productRated";
        } else {
            model.addAttribute("text", " product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @GetMapping("/{id}/fotoPerfil")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable long id) {

        Optional<Usuario> user = userService.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Blob profilePic = user.get().getFotoPerfil();

        try {
            byte[] picBytes = profilePic.getBytes(1, (int) profilePic.length());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(fotoBytes, headers, HttpStatus.OK);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
