package com.webapp08.pujahoy.dto;

import java.sql.Date;

public record OfferDTO(long id, double cost, Date hour, UserDTO user, ProductBasicDTO product){
    
}

