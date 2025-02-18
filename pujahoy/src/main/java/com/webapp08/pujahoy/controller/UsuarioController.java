package com.webapp08.pujahoy.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.service.UsuarioService;

import org.springframework.ui.Model;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuario/{id}") //Falta el modificarlo para q vaya por sesión
    public String usuario(Model model, @PathVariable String id){//HttpSesion sesion) {
        Optional<Usuario> user = usuarioService.findById(id);
		if (user.isPresent()) {
            model.addAttribute("Usuario",user.get());
			model.addAttribute("nombre", user.get().getNombre());
            model.addAttribute("nombreVisible", user.get().getNombreVisible());
            model.addAttribute("reputacion", user.get().getReputacion());
            model.addAttribute("contacto", user.get().getContacto());
            model.addAttribute("descripcion", user.get().getDescripcion());
			return "profile";
		} else {
			return "error"; //Falta crear página error
		}
    }

    @GetMapping("/usuario/editar")
    public String editarUsuario(){

        return "editProfile";
    }

    @GetMapping("/usuario/banear")
    public String banearUsuario(){

        return "banedProfile";
    }

    @GetMapping("/usuario/NuevoProducto")
    public String nuevoProducto(){

        return "newAuction";
    }

    @GetMapping("/usuario/verProductos")
    public String verProductos(){

        return "YourAuctions";
    }

    @GetMapping("/usuario/verCompras")
    public String verCompras(){

        return "YourWinningBids"; 
    }
}
