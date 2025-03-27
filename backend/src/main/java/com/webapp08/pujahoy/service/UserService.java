package com.webapp08.pujahoy.service;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.UserDTO;
import com.webapp08.pujahoy.dto.UserMapper;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.OfferRepository;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;
import com.webapp08.pujahoy.repository.UserModelRepository;

@Service
public class UserService {

	@Autowired
	private UserModelRepository repository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public PublicUserDTO findUser(Long id) {
		return mapper.toDTO(repository.findById(id).get());
	}

	public Blob getImageById(long id){
		Optional<UserModel> user = repository.findById(id);
		return user.get().getProfilePic();
	}

	public Resource getPostImage(long id) throws SQLException {

		UserModel user = repository.findById(id).orElseThrow();

		if (user.getProfilePic() != null) {
			return new InputStreamResource(user.getProfilePic().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public boolean getActiveById(long id){
		Optional<UserModel> user = repository.findById(id);
		return user.get().isActive();
	}

	public String getTypeById(long id){
		Optional<UserModel> user = repository.findById(id);
		return user.get().determineUserType();
	}


	public void finishProductsForUser(UserModel user) { // Responsible for finishing all the products of a user if he is banned
        List<Product> products = productRepository.findBySeller(user);
        for (Product product : products) {
            if (product.getState().equals("In progress")) {
                if (!product.getOffers().isEmpty()) {
                    for (Offer offer : product.getOffers()) {
                        offerRepository.delete(offer);
                    }
                }
                product.setOffers(null);
                product.setState("Finished");
                productRepository.save(product);
            }
        }
    }

    public void deleteProducts(UserModel user) { // Responsible for deleting all products from a user if he is unbanned
        List<Product> products = productRepository.findBySeller(user);
        for (Product product : products) {
            if (!product.getOffers().isEmpty()) {
                for (Offer offer : product.getOffers()) {
                    offerRepository.deleteById(offer.getId());
                }
            }
            Optional<Transaction> trans = transactionRepository.findByProduct(product);
            if (trans.isPresent()) {
                transactionRepository.deleteById(trans.get().getId());
            }
            productRepository.deleteById(product.getId());
        }
    }


	public Optional<PublicUserDTO> bannedUser(long id, PublicUserDTO user) throws SQLException {
		Optional<UserModel> optUser = repository.findById(user.getId());
		if (optUser.get().determineUserType() == "Administrator"){
			Optional<UserModel> oldUserOpt = repository.findById(id);
			if (!oldUserOpt.isPresent()) {
				return Optional.empty(); // The user dont exist
			}
			UserModel updatedUser = oldUserOpt.get();
			if (updatedUser.isActive()){
				this.finishProductsForUser(updatedUser);
			} else {
				this.deleteProducts(updatedUser);
			}
			updatedUser.setActive(!updatedUser.isActive());

			repository.save(updatedUser);

			return Optional.of(mapper.toDTO(updatedUser));
		}
		return Optional.empty(); // The changes is not for banned user
	}

	public PublicUserDTO replaceUser(PublicUserDTO updatedPostDTO) throws SQLException {

		UserModel oldPost = repository.findById(updatedPostDTO.getId()).orElseThrow();
		UserModel updatedPost = mapper.toDomain(updatedPostDTO);

		// Check and update the zip code
		if (updatedPost.getZipCode() != null && !updatedPost.getZipCode().equals(oldPost.getZipCode()) && updatedPost.getZipCode().toString().matches("\\d{5}")) {
			oldPost.setZipCode(updatedPost.getZipCode());
		}

		// Check and update the contact
		if (updatedPost.getContact() != null && !updatedPost.getContact().equals(oldPost.getContact()) && updatedPost.getContact().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			oldPost.setContact(updatedPost.getContact());
		}

		// Check and update the description
		if (updatedPost.getDescription() != null && !updatedPost.getDescription().equals(oldPost.getDescription())) {
			oldPost.setDescription(updatedPost.getDescription());
		}

		// Save the updated user
		repository.save(oldPost);

		return mapper.toDTO(oldPost);
	}

	public void replaceUserImage(long id, InputStream inputStream, long size) {
		UserModel user = repository.findById(id).orElseThrow();
		if(user.getImage() == null){
			throw new NoSuchElementException();
		}
		user.setProfilePic(BlobProxy.generateProxy(inputStream, size));
		repository.save(user);
	   }

	public Optional<UserModel> findByIdOLD(Long id) {
		return repository.findById(id);
	}

	public Optional<PublicUserDTO> findById(Long id) {
		Optional<UserModel> user = repository.findById(id);
		if (user.isPresent()){
			return Optional.of(mapper.toDTO(user.get()));
		} else {
			return Optional.empty();
		}
	}

	public void save(UserModel user) {
		repository.save(user);
	}

	public Optional<UserModel> findByNameOLD(String name) {
		return repository.findByName(name);
	}

	public Optional<PublicUserDTO> findByName(String name) {
		Optional<UserModel> user = repository.findByName(name);
		if (user.isPresent()){
			return Optional.of(mapper.toDTO(user.get()));
		} else {
			return Optional.empty();
		}
	}

	public Optional<UserModel> findByProductsOLD(ProductDTO product) {
		return repository.findByProducts(productRepository.findById(product.getId()).get());
	}

	public Optional<PublicUserDTO> findByProducts(ProductDTO product) {
		Optional<Product> pord = productRepository.findById(product.getId());
		if (pord.isPresent()){
			Optional<UserModel> user = repository.findByProducts(pord.get());
			if (user.isPresent()){
				return Optional.of(mapper.toDTO(user.get()));
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	public void createUser(UserDTO userDTO) {
		UserModel user = new UserModel(userDTO.getUsername(), 0, userDTO.getVisibleName(), userDTO.getEmail(), Integer.parseInt(userDTO.getZipCode()), userDTO.getDescription(), true,
                passwordEncoder.encode(userDTO.getPassword()), "USER");
		repository.save(user);
	}

}
