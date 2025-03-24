package com.webapp08.pujahoy.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webapp08.pujahoy.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product", target = "product")
    @Mapping(source = "seller", target = "seller")
    @Mapping(source = "buyer", target = "buyer")
    @Mapping(source = "cost", target = "cost")
    TransactionDTO toDTO(Transaction transaction);
      
    List<TransactionDTO> toDTOList(List<Transaction> transactions);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product", target = "product")
    @Mapping(source = "seller", target = "seller")
    @Mapping(source = "buyer", target = "buyer")
    @Mapping(source = "cost", target = "cost")
    Transaction toDomain(TransactionDTO transactionDTO);
}