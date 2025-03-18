package com.webapp08.pujahoy.dto;

public class PublicUserDTO {
    private Long id;
    private String name;
    private double reputation;
    private String visibleName;
    private String contact;
    private String description;
    private int zipCode;
    private String image;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getZipCode() {
        return zipCode;
    }
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
