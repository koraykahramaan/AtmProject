package com.koray.atmproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindAllAccountsResponse {

    private List<AccountResponse> accounts;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private String transaction;
}
