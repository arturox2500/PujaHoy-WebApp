package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Producto;
import com.webapp08.pujahoy.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
	private ProductoRepository repository;
}
