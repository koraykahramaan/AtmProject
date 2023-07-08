package com.koray.atmproject.dto;

import lombok.Data;

@Data
public class SendMoneyRequest {

    String accountNumber;
    int amount;
}
