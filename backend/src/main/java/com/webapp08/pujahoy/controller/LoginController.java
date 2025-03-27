package com.webapp08.pujahoy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.webapp08.pujahoy.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import com.webapp08.pujahoy.dto.UserDTO;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

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

    @GetMapping("/login")
    public String loginRedirect() { // Redirect to login view
        return "login";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam UserDTO userDTO, RedirectAttributes redirectAttributes, HttpServletRequest request) { // Form post for when someone attemps to register
        if (userService.findByName(userDTO.getUsername()).isPresent() || userDTO.getEmail().isBlank() || userDTO.getPassword().isBlank()
                || userDTO.getZipCode().isBlank() || userDTO.getUsername().isBlank() || userDTO.getVisibleName().isBlank()) {
            model.addAttribute("error", "Wrongs fields or user already exists");
            return "login";
        }

        if (!userDTO.getZipCode().matches("\\d{5}") ) {
            model.addAttribute("error", "The zip code must be a 5 digit number");
            return "login"; 
        }
     
        userService.createUser(userDTO);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) { // Logout trigger method
        request.getSession().invalidate();
        return "redirect:/"; 
    }

    @GetMapping("/permitsError")
    public String showErrorPage(Model model) { // Error in case of not authorized
        model.addAttribute("text", " You don't have permits"); 
        model.addAttribute("url", "/"); 
        return "pageError"; // Se debe llamar igual que la vista Mustache o Thymeleaf
    }

}
