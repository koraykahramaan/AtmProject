package com.koray.atmproject.controller;

import com.koray.atmproject.dto.*;
import com.koray.atmproject.exception.AlreadyHaveAccountException;
import com.koray.atmproject.model.Account;
import com.koray.atmproject.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable(name = "id") int id) {
        return accountService.getAccountById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FindAllAccountsResponse> findAll(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {

        return accountService.findAll(page, size);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody Account account) throws AlreadyHaveAccountException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return accountService.createAccount(account,userDetails.getUsername());
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MoneyTransactionResponse> sendMoneyToAccount(@RequestBody SendMoneyRequest sendMoneyRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return accountService.sendMoneyToAccount(sendMoneyRequest.getAccountNumber(),userDetails.getUsername(),sendMoneyRequest.getAmount());
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MoneyTransactionResponse> witdhrawMoney(@RequestBody WithdrawAndDepositMoneyRequest withdrawAndDepositMoneyRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return accountService.withdrawMoney(userDetails.getUsername(), withdrawAndDepositMoneyRequest.getAmount());
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<MoneyTransactionResponse> depositMoney(@RequestBody WithdrawAndDepositMoneyRequest withdrawAndDepositMoneyRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return accountService.depositMoney(userDetails.getUsername(),withdrawAndDepositMoneyRequest.getAmount());
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<AccountResponse> deleteAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return accountService.deleteAccount(userDetails.getUsername());
    }

}
