package com.webapp08.pujahoy.service;

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
import com.webapp08.pujahoy.dto.UserMapper;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Rating;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.OfferRepository;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.RatingRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;
import com.webapp08.pujahoy.repository.UserModelRepository;

@Service
public class UserService {

    @Autowired
	private UserModelRepository repository;

	@Autowired
	private RatingRepository  ratingRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

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


	public Optional<PublicUserDTO> bannedUser(long id, PublicUserDTO updatedUserDTO) throws SQLException {

		Optional<UserModel> oldUserOpt = repository.findById(id);
		if (!oldUserOpt.isPresent()){
			return Optional.empty(); //The user dont exist
		}
		UserModel oldUser = oldUserOpt.get();
		UserModel updatedUser = mapper.toDomain(updatedUserDTO);
		updatedUser.setId(id);
		if (mapper.toDTO(oldUser).changes(updatedUserDTO) == 0){

			if (oldUser.getImage() != null) {

				//Set the image in the updated post
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
		return Optional.empty(); //The changes is not for banned user
	}

	public void updateRating(UserModel user) { // Responsible for updating the reputation of a user
        List<Rating> ratings = ratingRepository.findAllBySeller(user);
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

	public PublicUserDTO replaceUser(long id, PublicUserDTO updatedPostDTO) throws SQLException {

		UserModel oldPost = repository.findById(id).orElseThrow();
		UserModel updatedPost = mapper.toDomain(updatedPostDTO);
		updatedPost.setId(id);

		if (oldPost.getImage() != null) {

			//Set the image in the updated post
			updatedPost.setProfilePic(BlobProxy.generateProxy(oldPost.getProfilePic().getBinaryStream(),
					oldPost.getProfilePic().length()));
			updatedPost.setImage(oldPost.getImage());
		}
		updatedPost.setRols(oldPost.getRols());
		updatedPost.setProducts(oldPost.getProducts());
		updatedPost.setPass(oldPost.getEncodedPassword());
		updatedPost.setActive(oldPost.isActive());

		repository.save(updatedPost);

		return mapper.toDTO(updatedPost);
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
