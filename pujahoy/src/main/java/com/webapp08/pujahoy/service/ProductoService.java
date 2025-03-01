package com.webapp08.pujahoy.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
	private ProductoRepository repository;
    
    public Optional<Producto> findById(long id) {
		return repository.findById(id);
	}

	public Optional<Producto> findById(Long id) {
		return repository.findById(id);
	}	

	public Producto save(Producto producto) {
		return repository.save(producto);
	}

	public void DeleteById(long id_producto) {
        repository.deleteById(id_producto);
    }

    public List<Producto> obtenerTodosLosProductos() {
        return repository.findAll();
    }

	public Page<Producto> obtenerProductosPaginados(String nombreVendedor, int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño);
        return repository.findByVendedor_Nombre(nombreVendedor, pageable);
    }

	public Page<Producto> obtenerProductosComprados(String nombreComprador, int pagina, int tamaño) {
		Pageable pageable = PageRequest.of(pagina, tamaño);
        return repository.findProductosCompradosPorUsuario(nombreComprador, pageable);
    }

}
