package com.webapp08.pujahoy.dto;

public class ProductBasicDTO {
    private long id;
    private double iniValue;
    private String name;
    private String imgURL;
    private PublicUserDTO seller;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImgURL() {
        return imgURL;
    }
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    public double getIniValue() {
        return iniValue;
    }
    public void setIniValue(double iniValue) {
        this.iniValue = iniValue;
    }
    public PublicUserDTO getSeller() {
        return seller;
    }
    public void setSeller(PublicUserDTO seller) {
        this.seller = seller;
    }
    
}
