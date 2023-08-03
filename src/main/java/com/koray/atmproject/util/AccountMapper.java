package com.koray.atmproject.util;

import com.koray.atmproject.dto.AccountResponse;
import com.koray.atmproject.dto.MoneyTransactionResponse;
import com.koray.atmproject.model.Account;

public class AccountMapper {

    public AccountResponse accountToAccountResponseMapper(Account account) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountId(account.getAccountId());
        accountResponse.setAmount(account.getAmount());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setUserid(account.getUserInfo().getUserId());
        return accountResponse;
    }

    public MoneyTransactionResponse accountToMoneyTransactionResponseMapper(Account account,String status,String message){
        MoneyTransactionResponse moneyTransactionResponse = new MoneyTransactionResponse();
        moneyTransactionResponse.setCurrentAmount(account.getAmount());
        moneyTransactionResponse.setMessage(message);
        if(status.equals(Response.FAIL)) {
            moneyTransactionResponse.setResponseStatus(Response.FAIL);
        }
        else {
            moneyTransactionResponse.setResponseStatus(Response.SUCCESS);
        }
        return moneyTransactionResponse;
    }

    public MoneyTransactionResponse accountToMoneyTransactionResponseMapper(MoneyTransactionResponse moneyTransactionResponse,Account account,String status,String message){
        moneyTransactionResponse.setCurrentAmount(account.getAmount());
        moneyTransactionResponse.setMessage(message);
        if(status.equals(Response.FAIL)) {
            moneyTransactionResponse.setResponseStatus(Response.FAIL);
        }
        else {
            moneyTransactionResponse.setResponseStatus(Response.SUCCESS);
        }
        return moneyTransactionResponse;
    }

}
