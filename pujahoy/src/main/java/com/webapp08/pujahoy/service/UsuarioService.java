package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.model.Usuario;
import com.webapp08.pujahoy.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
	private UsuarioRepository repository;
    
    public Optional<Usuario> findById(Long id) {
		return repository.findById(id);
	}

	public void save(Usuario user) {
		repository.save(user);
	}

}
