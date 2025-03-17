package com.webapp08.pujahoy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.UserMapper;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.UserModelRepository;

@Service
public class UserService {

    @Autowired
	private UserModelRepository repository;

	@Autowired
	private UserMapper mapper;
    
	public PublicUserDTO findUser(Long id) {
		return mapper.toDTO(repository.findById(id).get());
	}

    public Optional<UserModel> findById(Long id) {
		return repository.findById(id);
	}

	public void save(UserModel user) {
		repository.save(user);
	}

	public Optional<UserModel> findByName(String name) {
		return repository.findByName(name);
	}

	public Optional<UserModel> findByProducts(Product product){
		return repository.findByProducts(product);
	}
}
