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
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.service.ProductoService;
import com.webapp08.pujahoy.model.Valoracion;
import com.webapp08.pujahoy.service.ValoracionService;
//import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.service.TransaccionService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private TransaccionService transaccionService;

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

    @GetMapping() // Cuando acceden a su perfil
    public String verTuPerfilUsuario(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String username = principal.getName(); // Obtiene el nombre de usuario
            Optional<Usuario> user = usuarioService.findByNombre(username); // Busca en la base de datos
            if (user.isPresent()) {
                model.addAttribute("Usuario", user.get());
                model.addAttribute("id", user.get().getId());
                model.addAttribute("nombre", user.get().getNombre());
                model.addAttribute("nombreVisible", user.get().getNombreVisible());
                model.addAttribute("reputacion", user.get().getReputacion());
                model.addAttribute("codigoPostal", user.get().getCodigoPostal());
                model.addAttribute("contacto", user.get().getContacto());
                model.addAttribute("descripcion", user.get().getDescripcion());
                model.addAttribute("fotoPerfil", user.get().getFotoPerfil());
                model.addAttribute("admin", false);
                if (!user.get().isActivo()) { // Si esta baneado
                    model.addAttribute("baneado", true);
                    model.addAttribute("registrado", false);
                } else {
                    model.addAttribute("baneado", false);
                    model.addAttribute("registrado", true);
                }
                return "profile";
            } else {
                model.addAttribute("texto", "user not found");
                model.addAttribute("url", "/");
            }
        }
        model.addAttribute("texto", "you must be logged in");
        model.addAttribute("url", "/");
        return "pageError";
    }

    @GetMapping("/{id}") // El id es el del producto
    // Doy por hecho q el valor asociado a la sesión es el id del usuario
    public String verPerfilAjeno(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<Producto> product = productoService.findById(id);
        if (product.isPresent()) {
            Optional<Usuario> vendedor = usuarioService.findByProductos(product.get());
            if (vendedor.isPresent()) {
                Principal principal = request.getUserPrincipal();
                Optional<Usuario> user;
                String tipo;
                if (principal != null) { // Si esta autenticado
                    String username = principal.getName(); // Obtiene el nombre de usuario
                    user = usuarioService.findByNombre(username); // Busca en la base de datos
                    tipo = user.get().determinarTipoUsuario();
                    if (user.get().getId() == vendedor.get().getId()) { // Si el perfil es el suyo propio
                        return "redirect:/usuario";
                    }
                } else {
                    user = null;
                    tipo = "";
                }
                model.addAttribute("registrado", false);
                model.addAttribute("baneado", false);
                model.addAttribute("Usuario", vendedor.get());
                model.addAttribute("id", vendedor.get().getId());
                model.addAttribute("nombre", vendedor.get().getNombre());
                model.addAttribute("nombreVisible", vendedor.get().getNombreVisible());
                model.addAttribute("reputacion", vendedor.get().getReputacion());
                model.addAttribute("contacto", vendedor.get().getContacto());
                model.addAttribute("descripcion", vendedor.get().getDescripcion());
                if (tipo.equals("Administrador")) {
                    model.addAttribute("admin", true);
                } else { // Usuario registrado
                    model.addAttribute("admin", false);
                }
                if (vendedor.get().isActivo()) { // Si esta baneado
                    model.addAttribute("baneado", false);
                } else {
                    model.addAttribute("baneado", true);
                }
                return "profile";
            } else {
                model.addAttribute("texto", "seller not found");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("texto", "product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @PostMapping()
    public String editarPerfil(Model model, @RequestParam long id, @RequestParam String contacto,
            @RequestParam String descripcion, @RequestParam String codigoPostal,
            @RequestParam(required = false) MultipartFile fotoPerfil) throws IOException, SQLException {

        Usuario usuario = usuarioService.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!codigoPostal.matches("\\d{5}") || !contacto.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        ) {
            return "redirect:/usuario"; 
        }
        
        // Actualizar los datos del usuario
        usuario.setContacto(contacto);
        usuario.setDescripcion(descripcion);
        usuario.setCodigoPostal(Integer.parseInt(codigoPostal));

        // Si se ha subido una nueva foto, actualiza el perfil
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            byte[] fotoBytes = fotoPerfil.getBytes();
            Blob fotoBlob = new SerialBlob(fotoBytes);
            usuario.setFotoPerfil(fotoBlob);
        }

        usuarioService.save(usuario); // Guardar los cambios

        return "redirect:/usuario"; // Redirigir al perfil actualizado
    }

    @PostMapping("/{id}/banear")
    public String bannedUser(Model model, @PathVariable String id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            Optional<Usuario> admin = usuarioService.findByNombre(principal.getName());
            Optional<Usuario> user = usuarioService.findById(Long.parseLong(id));
            String tipo = admin.get().determinarTipoUsuario();
            if (user.isPresent() && tipo.equals("Administrador")) {
                Boolean activo = user.get().isActivo();
                user.get().changeActivo();
                usuarioService.save(user.get());
                if (activo) {
                    model.addAttribute("text", "user banned");
                } else {
                    model.addAttribute("text", "user unbanned");
                }
                return "bannedProfile";
            } else if (!tipo.equals("Administrador")) {
                model.addAttribute("texto", "you are not allow to banned users");
                model.addAttribute("url", "/");
            } else {
                model.addAttribute("texto", "user not found");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("texto", "you must be logged in");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @GetMapping("/producto_template")
    public String verProductos(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); // Obtener nombre de usuario
            Optional<Usuario> user = usuarioService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> productos = productoService.obtenerProductosPaginados(username, pagina, tamaño);

                model.addAttribute("productos", productos); // Pasamos la página completa
                return "producto_template";
            }
        }

        model.addAttribute("texto", "Usted no está autenticado");
        return "pageError";
    }

    @GetMapping("/verProductos")
    public String verProductosIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); // Obtener nombre de usuario
            Optional<Usuario> user = usuarioService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> productos = productoService.obtenerProductosPaginados(username, pagina, tamaño);
                Boolean button = true;
                if (productos.isEmpty()) {
                    button = false;
                }

                model.addAttribute("button", button);
                model.addAttribute("productos", productos); // Pasamos la página completa
                return "YourProducts";
            }
        }

        model.addAttribute("texto", "Usted no está autenticado");
        return "pageError";
    }

    @GetMapping("/producto_template_compras")
    public String verProductosCompras(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); // Obtener nombre de usuario
            Optional<Usuario> user = usuarioService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> productos = productoService.obtenerProductosComprados(username, pagina, tamaño);

                model.addAttribute("productos", productos); // Pasamos la página completa
                return "producto_template";
            }
        }

        model.addAttribute("texto", "Usted no está autenticado");
        return "pageError";
    }

    @GetMapping("/verCompras")
    public String verProductosComprasIni(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName(); // Obtener nombre de usuario
            Optional<Usuario> user = usuarioService.findByNombre(username);

            if (user.isPresent()) {
                Page<Producto> productos = productoService.obtenerProductosComprados(username, pagina, tamaño);
                Boolean button = true;
                if (productos.isEmpty()) {
                    button = false;
                }

                model.addAttribute("button", button);
                model.addAttribute("productos", productos); // Pasamos la página completa
                return "YourWinningBids";
            }
        }

        model.addAttribute("texto", "Usted no está autenticado");
        return "pageError";
    }

    @GetMapping("/NuevoProducto")
    public String nuevoProducto() {
        return "newAuction";
    }

    @PostMapping("/submit_auction")
    public String publicarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("valorini") double precio,
            @RequestParam("duracion") int duracion,
            @RequestParam("estado") String estado,
            @RequestParam("imagen") MultipartFile imagenFile,
            HttpServletRequest request,
            Model model) {

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            model.addAttribute("texto", "Usuario no encontrado");
            return "pageError";
        }

        Optional<Usuario> usuario = usuarioService.findByNombre(principal.getName());

        if (usuario.isEmpty()) {
            model.addAttribute("texto", "Usuario no encontrado en la base de datos");
            return "pageError";
        }

        try {
            // Obtener la fecha y hora actual en java.sql.Date
            Date horaIni = new Date(System.currentTimeMillis());
            Date horaFin = new Date(horaIni.getTime() + (long) duracion * 24 * 60 * 60 * 1000);

            // Convertir la imagen a Blob si existe
            Blob imagen = null;
            if (!imagenFile.isEmpty()) {
                imagen = BlobProxy.generateProxy(imagenFile.getInputStream(), imagenFile.getSize());
            }

            // Crear el producto con el usuario obtenido
            Producto producto = new Producto(nombre, descripcion, precio, horaIni, horaFin, estado, imagen,
                    usuario.get());

            // Guardar el producto en la base de datos
            productoService.save(producto);

            model.addAttribute("producto", producto);
            return "redirect:/producto/" + producto.getId();

        } catch (Exception e) {
            model.addAttribute("texto", "Error al procesar el producto: " + e.getMessage());
            return "pageError";
        }
    }

    @GetMapping("/{id}/rate") // Te envia a la pagina de valorar
    public String irValorar(Model model, @PathVariable long id, HttpServletRequest request) {
        Optional<Producto> product = productoService.findById(id);
        if (product.isPresent()) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                model.addAttribute("texto", "you must be logged in");
                model.addAttribute("url", "/");
                return "pageError";
            }
            Optional<Transaccion> trans = transaccionService.findByProducto(product.get());
            if (trans.isEmpty()) {
                model.addAttribute("texto", "this product has not been sold");
                model.addAttribute("url", "/");
                return "pageError";
            }
            Optional<Usuario> user = usuarioService.findById(trans.get().getComprador().getId());
            Optional<Usuario> user1 = usuarioService.findByNombre(principal.getName());
            if (user.isPresent() && user1.isPresent()) {
                if (user.get().determinarTipoUsuario().equals("Usuario Registrado")
                        && user1.get().getId().equals(user.get().getId())) {
                    model.addAttribute("id", id);
                    return "ratingProduct";
                } else {
                    model.addAttribute("texto", "this product is not yours");
                    model.addAttribute("url", "/");
                }
            } else {
                model.addAttribute("texto", "buyer not exist");
                model.addAttribute("url", "/");
            }
        } else {
            model.addAttribute("texto", "product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    public void updateRating(Usuario user) {
        List<Valoracion> valoraciones = valoracionService.findAllByVendedor(user);
        if (valoraciones.isEmpty()) {
            return; // Evitar división por cero
        }
        int amount = 0;
        for (Valoracion val : valoraciones) {
            amount += val.getPuntuacion();
        }
        double mean = (double) amount / valoraciones.size();

        user.setReputacion(mean);
        usuarioService.save(user);
    }

    @PostMapping("/{id}/rated")
    public String valorarProducto(Model model, @PathVariable long id, @RequestParam int rating) {
        if (rating < 1 || rating > 5) {
            model.addAttribute("texto", "the rated must be between 1 and 5");
            model.addAttribute("url", "/");
            return "pageError";
        }
        Optional<Producto> product = productoService.findById(id);
        if (product.isPresent()) {
            Optional<Valoracion> existingVal = valoracionService.findByProducto(product.get());
            if (existingVal.isPresent()) {
                model.addAttribute("texto", "This product has already been rated");
                model.addAttribute("url", "/");
                return "pageError";
            }
            Valoracion val = new Valoracion(product.get().getVendedor(), product.get(), rating);
            valoracionService.save(val);
            this.updateRating(product.get().getVendedor());
            model.addAttribute("id", product.get().getId());
            return "productRated";
        } else {
            model.addAttribute("texto", "product not found");
            model.addAttribute("url", "/");
        }
        return "pageError";
    }

    @GetMapping("/{id}/fotoPerfil")
    public ResponseEntity<byte[]> getFotoPerfil(@PathVariable long id) {

        Optional<Usuario> user = usuarioService.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Blob fotoPerfil = user.get().getFotoPerfil();

        try {
            byte[] fotoBytes = fotoPerfil.getBytes(1, (int) fotoPerfil.length());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(fotoBytes, headers, HttpStatus.OK);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
