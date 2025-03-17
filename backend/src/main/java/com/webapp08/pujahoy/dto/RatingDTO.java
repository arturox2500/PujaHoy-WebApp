package com.webapp08.pujahoy.dto;

public class RatingDTO {

    private long id;
    private long idSeller;
    private long idProduct;
    private int rating;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getIdSeller() {
        return idSeller;
    }
    public void setIdSeller(long idSeller) {
        this.idSeller = idSeller;
    }
    public long getIdProduct() {
        return idProduct;
    }
    public void setIdProduct(long idProduct) {
        this.idProduct = idProduct;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
