package com.webapp08.pujahoy.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity(name = "USERS")
public class UserModel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double reputation;
    private String visibleName;
    private String contact;
    private String description;
    private boolean active;
    
    @Lob
    private Blob profilePic;
    private int zipCode;

    @OneToMany(mappedBy="seller",cascade = CascadeType.ALL)
    private List<Product> products;

    private String encodedPassword;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> rols;

    protected UserModel(){

    }

    public UserModel(String name, double reputation, String visibleName, String contact, int zipCode,String description, boolean active, String encodedPassword, String... rols){
        this.name = name;
        this.reputation = reputation;
        this.encodedPassword = encodedPassword;
        this.rols = List.of(rols);
        this.contact = contact;
        this.description = description;
        this.active = active;
        this.visibleName = visibleName;
        this.products = null;
        this.profilePic= UploadStandardProfilePic();
        this.zipCode = zipCode;
    }

    public Long getId() {
        return id;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public void setProfilePic(Blob profilePic) {
        this.profilePic = profilePic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getProfilePic() {
        return profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getReputation() {
        return reputation;
    }

    public void setReputation(double reputation) {
        this.reputation = reputation;
    }

    public String getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void changeActive(){
        this.active = !this.active;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setPass(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRols() {
        return rols;
    }

    public void setRols(List<String> rols) {
        this.rols = rols;
    }  
    
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String determineUserType() {
        if (this.getRols().contains("ADMIN")) {
            return "Administrator";
        } else if (this.getRols().contains("USER")) {
            return "Registered User";
        } else {
            return "Unknown";
        }
    }

    private Blob UploadStandardProfilePic() {
        try {
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("static/img/default-profile.jpg");
            if (imageStream != null) {
                byte[] imageBytes = imageStream.readAllBytes();
                return new SerialBlob(imageBytes);
            }
            return null;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
