package com.webapp08.pujahoy.service;

import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import com.webapp08.pujahoy.dto.PublicUserDTO;
import com.webapp08.pujahoy.dto.UserDTO;
import com.webapp08.pujahoy.dto.UserMapper;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.UserModelRepository;

@Service
public class UserService {

	@Autowired
	private UserModelRepository repository;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OfferService offerService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserMapper mapper;

	public PublicUserDTO findUser(Long id) {
		return mapper.toDTO(repository.findById(id).get());
	}

	public Resource getPostImage(long id) throws SQLException {

		UserModel user = repository.findById(id).orElseThrow();

		if (user.getProfilePic() != null) {
			return new InputStreamResource(user.getProfilePic().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}

	  public void finishProductsForUser(UserModel user) { // Responsible for finishing all the products of a user if he is banned
        List<Product> products = productService.findBySeller(user);
        for (Product product : products) {
            if (product.getState().equals("In progress")) {
                if (!product.getOffers().isEmpty()) {
                    for (Offer offer : product.getOffers()) {
                        offerService.delete(offer);
                    }
                }
                product.setOffers(null);
                product.setState("Finished");
                productService.save(product);
            }
        }
    }

    public void deleteProducts(UserModel user) { // Responsible for deleting all products from a user if he is unbanned
        List<Product> products = productService.findBySeller(user);
        for (Product product : products) {
            if (!product.getOffers().isEmpty()) {
                for (Offer offer : product.getOffers()) {
                    offerService.deleteById(offer.getId());
                }
            }
            Optional<Transaction> trans = transactionService.findByProduct(product);
            if (trans.isPresent()) {
                transactionService.deleteById(trans.get().getId());
            }
            productService.deleteById(product.getId());
        }
    }


	public Optional<PublicUserDTO> bannedUser(long id, PublicUserDTO updatedUserDTO) throws SQLException {

		Optional<UserModel> oldUserOpt = repository.findById(id);
		if (!oldUserOpt.isPresent()) {
			return Optional.empty(); // The user dont exist
		}
		UserModel oldUser = oldUserOpt.get();
		UserModel updatedUser = mapper.toDomain(updatedUserDTO);
		updatedUser.setId(id);
		if (mapper.toDTO(oldUser).changes(updatedUserDTO) == 0) {

			if (oldUser.getImage() != null) {

				// Set the image in the updated post
				updatedUser.setProfilePic(BlobProxy.generateProxy(oldUser.getProfilePic().getBinaryStream(),
						oldUser.getProfilePic().length()));
				updatedUser.setImage(oldUser.getImage());
			}
			updatedUser.setRols(oldUser.getRols());
			updatedUser.setProducts(oldUser.getProducts());
			updatedUser.setPass(oldUser.getEncodedPassword());
			if (oldUser.isActive()){
				this.finishProductsForUser(updatedUser);
			} else {
				this.deleteProducts(updatedUser);
			}
			updatedUser.setActive(!oldUser.isActive());

			repository.save(updatedUser);

			return Optional.of(mapper.toDTO(updatedUser));
		}
		return Optional.empty(); // The changes is not for banned user
	}

	public void updateRating(UserModel user) { // Responsible for updating the reputation of a user
		List<Rating> ratings = ratingService.findAllBySeller(user);
		if (ratings.isEmpty()) {
			return;
		}
		int amount = 0;
		for (Rating val : ratings) {
			amount += val.getRating();
		}
		double mean = (double) amount / ratings.size();

		user.setReputation(mean);
		this.save(user);
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

	public Optional<UserModel> findById(Long id) {
		return repository.findById(id);
	}

	public void save(UserModel user) {
		repository.save(user);
	}

	public Optional<UserModel> findByName(String name) {
		return repository.findByName(name);
	}

	public Optional<UserModel> findByProducts(Product product) {
		return repository.findByProducts(product);
	}

}
