package com.koray.atmproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private int accountId;
    private int amount;
    private int userid;
    private String accountNumber;

    public void setNull() {

    }
}
