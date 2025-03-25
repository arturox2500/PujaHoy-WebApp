package com.webapp08.pujahoy.service;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import com.webapp08.pujahoy.dto.ProductBasicDTO;
import com.webapp08.pujahoy.dto.ProductBasicMapper;
import com.webapp08.pujahoy.dto.ProductDTO;
import com.webapp08.pujahoy.dto.ProductMapper;
import com.webapp08.pujahoy.model.Offer;
import com.webapp08.pujahoy.model.Product;
import com.webapp08.pujahoy.model.Transaction;
import com.webapp08.pujahoy.model.UserModel;
import com.webapp08.pujahoy.repository.OfferRepository;
import com.webapp08.pujahoy.repository.ProductRepository;
import com.webapp08.pujahoy.repository.TransactionRepository;

import org.springframework.core.io.Resource;

@Service
public class ProductService {

    @Autowired
	private ProductRepository repository;
	@Autowired
	private ProductMapper mapper;
	@Autowired
	private ProductBasicMapper basicMapper;
	@Autowired
    private TransactionRepository transactionRepository;
	@Autowired
    private OfferRepository offerRepository;
	@Autowired
    private OfferService offerService;
    
    public Optional<Product> findByIdOLD(long id) {
		return repository.findById(id);
	}

	public Optional<ProductDTO> findById(long id) {
		Optional<Product> prod = repository.findById(id);
		if (prod.isPresent()){
			return Optional.of(mapper.toDTO(prod.get()));
		} else {
			return Optional.empty();
		}
	}

	public Product save(Product product) {
		return repository.save(product);
	}

	public void deleteById(long id_product) {
		Optional<Product> existingProduct = this.findByIdOLD(id_product);
		Optional<Transaction> trans = transactionRepository.findByProduct(existingProduct.get());
        if (trans.isPresent()){
            transactionRepository.deleteById(trans.get().getId());  
        }
		for (Offer offer : existingProduct.get().getOffers()) {
			offerRepository.delete(offer);
		}
        repository.deleteById(id_product);
    }

	public List<Product> findBySeller(UserModel user) {
		return repository.findBySeller(user);
	}

	//Search all
    public Page<Product> obtainAllProductOrdersByReputation(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findAllOrderedBySellerReputation(pageable);
    }
	//Search only In progress
	public Page<Product> obtainAllProductOrdersInProgressByReputation(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findByStateInProgressOrderedBySellerReputation(pageable);
    }
	//Search all
    public Page<ProductBasicDTO> obtainAllProductOrdersByReputationDTO(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findAllOrderedBySellerReputation(pageable).map(this::toDTO);
    }
	//Search only In progress
	public Page<ProductBasicDTO> obtainAllProductOrdersInProgressByReputationDTO(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findByStateInProgressOrderedBySellerReputation(pageable).map(this::toDTO);
    }

	public Page<Product> obtainAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

	public Page<Product> obtainPaginatedProducts(String sellerName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findBySeller_Name(sellerName, pageable);
    }

	public Page<Product> obtainProductsBuyed(String buyerName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return repository.findBoughtProductsByUser(buyerName, pageable);
    }

	public ProductDTO findProduct(Long id) {
		return mapper.toDTO(repository.findById(id).get());
	}

	public Resource getPostImage(long id) throws SQLException {
		Product product = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));

		if (product.getImage() != null) {
			return new InputStreamResource(product.getImage().getBinaryStream());
		} else {
			throw new NoSuchElementException("No image");
		}
	}

	public void savePostImage(long id, MultipartFile imageFile) throws IOException {
		Product post = repository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Not found"));

		byte[] imageBytes = imageFile.getBytes();
		try {
			Blob imageBlob = new SerialBlob(imageBytes); 
			post.setImage(imageBlob);
			repository.save(post); 
		} catch (SQLException e) {
			throw new IOException("Error", e);
		}
	}



	public Page<ProductBasicDTO> findProducts(Pageable pageable) {
    return repository.findAll(pageable).map(this::toDTO);   
	}


	public Page<ProductBasicDTO> findProductsByUser(Pageable pageable, Long id) {
		return repository.findBySeller_Id(pageable, id).map(this::toDTO);		
	}

	public Page<ProductBasicDTO> findBoughtProductsByUser(Pageable pageable, Long id) {
		return repository.findBoughtProductsByUserID(pageable, id).map(this::toDTO);
	}

	private ProductBasicDTO toDTO(Product product){
		return basicMapper.toDTO(product);
	}

	public Offer PlaceABid(Product product,double cost, UserModel bidder) {
		//Get last bid
        Offer lastOffer = offerService.findLastOfferByProduct(product.getId());
        //Set min cost
        double actualPrice;
        if (lastOffer != null) {
            actualPrice = lastOffer.getCost();
        } else {
            actualPrice = product.getIniValue() - 1;
        }
        if(cost>actualPrice){
            long currentTime = System.currentTimeMillis();
            Date currentDate = new Date(currentTime); 
            Offer newOffer=new Offer(bidder,product,cost,currentDate);

            product.getOffers().add(newOffer); 
            offerService.save(newOffer);
            this.save(product);
			return newOffer;
		}else{
			return null;
		}
	}
}
