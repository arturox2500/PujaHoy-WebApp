package com.webapp08.pujahoy.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.repository.OfertaRepository;
import com.webapp08.pujahoy.service.OfertaService;
import com.webapp08.pujahoy.service.ProductoService;
import com.webapp08.pujahoy.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ProductoController {

    @Autowired
    private ProductoService ProductoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private OfertaService OfertaService;

    @PostMapping("/product/{id_producto}/delete")
    public String delteProduct(Model model,@PathVariable long id_producto) {
        Optional<Producto> product = ProductoService.findById(id_producto);
        
        if (product.isPresent()) {
            ProductoService.DeleteById(id_producto);
			return "/";
		} else {
            model.addAttribute("texto", "Error al borrar producto");
			return "error"; 
		}
        
    }
    @GetMapping("/producto/{id_producto}")
    public String mostrarProducto(@PathVariable long id_producto, Model model, HttpServletRequest request) {
        Optional<Producto> productoOpt = ProductoService.findById(id_producto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);

            // Obtener usuario de la sesión
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                String username = principal.getName(); // Obtiene el nombre de usuario
                Optional<Usuario> user = usuarioService.findByNombre(username); // Busca en la base de datos
                Usuario usuario = user.orElse(null);
            
                if (usuario != null && "Administrador".equalsIgnoreCase(usuario.determinarTipoUsuario())) {
                    model.addAttribute("admin", true);
                    model.addAttribute("usuario_autenticado", false);
                } else if (usuario != null) {
                    model.addAttribute("admin", false);
                    model.addAttribute("usuario_autenticado", true);
                }else{
                    model.addAttribute("admin", false);
                    model.addAttribute("usuario_autenticado", false);
                }
            }
            return "product";
        } else {
            return "error";
        }
    }  
    @PostMapping("/product/{id_producto}/place-bid")
    public String placeBid(@PathVariable long id_producto, HttpServletRequest request, Model model) {
        // Buscar el producto
        Optional<Producto> productoOpt = ProductoService.findById(id_producto);
        
        if (!productoOpt.isPresent()) {
            model.addAttribute("texto", "Producto no encontrado.");
            return "error";
        }

        Producto producto = productoOpt.get();

        // Obtener el usuario autenticado
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            model.addAttribute("texto", "Debes iniciar sesión para hacer una oferta.");
            return "error";
        }

        String username = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByNombre(username);

        if (!usuarioOpt.isPresent()) {
            model.addAttribute("texto", "Usuario no encontrado.");
            return "error";
        }

        Usuario usuario = usuarioOpt.get();

        // Obtener la última oferta del producto desde la base de datos
        Oferta ultimaOferta = OfertaService.findLastOfferByProduct(id_producto);

        // Determinar el nuevo precio de la oferta
        double nuevoPrecio = (ultimaOferta != null) ? ultimaOferta.getCoste() + 10.0 : producto.getValorini();

        // Crear y guardar la nueva oferta
        Oferta nuevaOferta = new Oferta(usuario, producto, nuevoPrecio, new Date(id_producto));

        // Agregar la oferta a la lista de ofertas del producto
        producto.getOfertas().add(nuevaOferta);

        // Guardar la oferta y actualizar el producto
        OfertaService.save(nuevaOferta);
        ProductoService.save(producto);

        return "redirect:/producto/" + id_producto;
    }
}
    