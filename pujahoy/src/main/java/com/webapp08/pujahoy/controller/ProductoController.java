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

import com.webapp08.pujahoy.model.Oferta;
import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.model.Transaccion;
import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.service.OfertaService;
import com.webapp08.pujahoy.service.ProductoService;
import com.webapp08.pujahoy.service.TransaccionService;
import com.webapp08.pujahoy.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private OfertaService OfertaService;

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


    @GetMapping("/")
        public String listarProductos(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {    
        Page<Producto> productos = productoService.obtenerTodosLosProductos(page,size); // Cambiamos a lista

        Boolean button = true;
        if (productos.isEmpty()){
                button = false;
        }

        // Agregar atributos al modelo
        model.addAttribute("button", button);
        model.addAttribute("productos", productos);

        return "index"; // Retorna la plantilla index.mustache
    }
    
    @GetMapping("/producto_template_index")
    public String verProductos(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Page<Producto> productos = productoService.obtenerTodosLosProductos(page,size);
                
        model.addAttribute("productos", productos); // Pasamos la página completa
        return "producto_template";
            

    }

    @PostMapping("/product/{id_producto}/delete")
    public String delteProduct(Model model,@PathVariable long id_producto) {
        Optional<Producto> product = productoService.findById(id_producto);
        
        if (product.isPresent()) {
            if (!product.get().getOfertas().isEmpty()) {
                for (Oferta oferta : product.get().getOfertas()) {
                    OfertaService.deleteById(oferta.getId());
                }
            } 
            Optional<Transaccion> trans = transaccionService.findByProducto(product.get());
            if (trans.isPresent()) {
                transaccionService.deleteById(trans.get().getId());
            }
            productoService.DeleteById(id_producto);
			return "redirect:/";
		} else {
            model.addAttribute("texto", "Error al borrar producto");
            model.addAttribute("url", "/");
			return "errorPage"; 
		}
        
    }
    @GetMapping("/producto/{id_producto}")
    public String mostrarProducto(@PathVariable long id_producto, Model model, HttpServletRequest request) {
        Optional<Producto> productoOpt = productoService.findById(id_producto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);

            long actualTime = System.currentTimeMillis();
            

            if(producto.getHoraFin().getTime()<=(actualTime) && producto.getEstado().equals("En curso")){
                producto.setEstado("Finalizado");
                List<Oferta> ofertas = producto.getOfertas();
                if (!ofertas.isEmpty()) {
                    Oferta ultimaOferta = ofertas.get(ofertas.size() - 1);

                    // New transaction
                    Transaccion transaccion = new Transaccion(producto,producto.getVendedor(),ultimaOferta.getUsuario(),ultimaOferta.getCoste());

                    // Save transaction
                    transaccionService.save(transaccion);
                }
            }
            
            productoService.save(producto);

            // Obtener usuario de la sesión
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                String username = principal.getName(); // Obtiene el nombre de usuario
                Optional<Usuario> user = usuarioService.findByNombre(username); // Busca en la base de datos
                Usuario usuario = user.orElse(null);
                List<Oferta> ofertas = producto.getOfertas();
                double[] costes;
                int numOfertas = ofertas.size();
                if (numOfertas > 0){
                    costes = new double[numOfertas];
                    for(int i = 0; i < numOfertas; i++){
                        costes[i] = ofertas.get(i).getCoste();
                    }
                } else {
                    costes = new double[0];
                }
                
                model.addAttribute("costes", Arrays.toString(costes));

                model.addAttribute("codigoPostal", producto.getVendedor().getCodigoPostal());

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
                if(producto.getEstado().equals("Finalizado")){
                    model.addAttribute("Finalizado", true);
                    
                    if (!ofertas.isEmpty()) {
                        Oferta ultimaOferta = ofertas.get(ofertas.size() - 1);
                    
                        if(ultimaOferta.getUsuario().equals(usuario)){
                            model.addAttribute("Ganador", true);
                        }else{
                            model.addAttribute("Ganador", false);
                        }
                    }
                }else{
                    model.addAttribute("Finalizado", false);
                }
            
            }
            return "product";
        } else {
            return "error";
        }
    }  
    @PostMapping("/product/{id_producto}/place-bid")
    public String placeBid(@PathVariable long id_producto, HttpServletRequest request, Model model) {
        
        Optional<Producto> productoOpt = productoService.findById(id_producto);
        
        if (!productoOpt.isPresent()) {
            model.addAttribute("texto", "Producto no encontrado.");
            return "error";
        }

        Producto producto = productoOpt.get();

        
        Principal principal = request.getUserPrincipal();

        String username = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByNombre(username);

        if (!usuarioOpt.isPresent()) {
            model.addAttribute("texto", "Usuario no encontrado.");
            return "error";
        }

        Usuario usuario = usuarioOpt.get();

        Oferta ultimaOferta = OfertaService.findLastOfferByProduct(id_producto);

        double nuevoPrecio;
        if(ultimaOferta != null){
             nuevoPrecio = ultimaOferta.getCoste() + 10.0;
        }else{//no tiene pujas a si que el valor inicial
             nuevoPrecio = producto.getValorini();
        }

        //fecha actual
        long actualTime = System.currentTimeMillis();
        Date fechaActual = new Date(actualTime);

        Oferta nuevaOferta = new Oferta(usuario, producto, nuevoPrecio, fechaActual);

        
        producto.getOfertas().add(nuevaOferta); //añadimos oferta a la lista

        OfertaService.save(nuevaOferta);
        productoService.save(producto);

        return "redirect:/producto/" + id_producto;
    }

@GetMapping("/producto/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Producto> op = productoService.findById(id);

		if (op.isPresent() && op.get().getImagen() != null) {
			
			Blob image = op.get().getImagen();
			Resource file = new InputStreamResource(image.getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(image.length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
    