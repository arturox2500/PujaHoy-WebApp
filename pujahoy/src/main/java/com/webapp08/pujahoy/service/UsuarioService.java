package com.webapp08.pujahoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
	private UsuarioRepository repository;
    
}
