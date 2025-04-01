package com.webapp08.pujahoy.dto;

import java.sql.Date;
import java.util.List;


public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double iniValue;
    private Date iniHour;
    private Date endHour;
    private String state;
    private String imgURL;
    private PublicUserDTO seller;
    private Long duration;
    
    private List<OfferBasicDTO> offers;
    

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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getIniValue() {
        return iniValue;
    }
    public void setIniValue(Double iniValue) {
        this.iniValue = iniValue;
    }
    public Date getIniHour() {
        return iniHour;
    }
    public void setIniHour(Date iniHour) {
        this.iniHour = iniHour;
    }
    public Date getEndHour() {
        return endHour;
    }
    public void setEndHour(Date endHour) {
        this.endHour = endHour;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getImgURL() {
        return imgURL;
    }
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    public PublicUserDTO getSeller() {
        return seller;
    }
    public void setSeller(PublicUserDTO seller) {
        this.seller = seller;
    }
    public List<OfferBasicDTO> getOffers() {
        return offers;
    }
    public void setOffers(List<OfferBasicDTO> offers) {
        this.offers = offers;
    }

    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }
    public boolean isActive() {
        long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime);
    
        return iniHour != null && endHour != null && !currentDate.before(iniHour) && !currentDate.after(endHour);
    }
    
    
}
