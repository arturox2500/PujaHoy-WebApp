package com.webapp08.pujahoy.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Usuario;
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
}
    