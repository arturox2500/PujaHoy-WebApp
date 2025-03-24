package com.webapp08.pujahoy.dto;

import java.sql.Date;

public class TransactionDTO {
    private long id;
    private long productId;
    private long buyerId;
    private double finalBidAmount;
    private Date transactionDate;

    public TransactionDTO() {
    }

    public TransactionDTO(long id, long productId, long buyerId, double finalBidAmount, Date transactionDate) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
        this.finalBidAmount = finalBidAmount;
        this.transactionDate = transactionDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public double getFinalBidAmount() {
        return finalBidAmount;
    }

    public void setFinalBidAmount(double finalBidAmount) {
        this.finalBidAmount = finalBidAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
