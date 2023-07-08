package com.koray.atmproject.dto;

import lombok.Data;

@Data
public class MoneyTransactionResponse {

    String responseStatus;
    int currentAmount;
    String message;
    String transactionName;

}
