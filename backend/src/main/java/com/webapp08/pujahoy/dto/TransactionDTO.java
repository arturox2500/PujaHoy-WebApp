package com.webapp08.pujahoy.dto;



public record TransactionDTO(
    long id,
    ProductBasicDTO product,
    UserBasicDTO seller,
    UserBasicDTO buyer,
    double cost
) {}