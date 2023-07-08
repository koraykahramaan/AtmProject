package com.koray.atmproject.service;


import com.koray.atmproject.dto.AccountResponse;
import com.koray.atmproject.dto.FindAllAccountsResponse;
import com.koray.atmproject.dto.MoneyTransactionResponse;
import com.koray.atmproject.exception.AccountNotFoundException;
import com.koray.atmproject.exception.AccountwithAccountNumberNotFoundException;
import com.koray.atmproject.exception.AlreadyHaveAccountException;
import com.koray.atmproject.model.Account;
import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.repository.AccountRepository;
import com.koray.atmproject.repository.UserInfoRepository;
import com.koray.atmproject.util.Response;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserInfoRepository userInfoRepository;

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public ResponseEntity<AccountResponse> getAccountById(int id) {
        AccountResponse accountResponse = new AccountResponse();

        logger.info("Get Account By Id Started with id : " + id);

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        accountResponse.setAccountId(id);
        accountResponse.setAmount(account.getAmount());
        accountResponse.setUserid(account.getUserInfo().getUserId());

        logger.info("Response {}", accountResponse);
        return new ResponseEntity<AccountResponse>(accountResponse, HttpStatus.OK);
    }


    public ResponseEntity<FindAllAccountsResponse> findAll(int page, int size) {

        FindAllAccountsResponse findAllAccountsResponse = new FindAllAccountsResponse();

        List<Account> accounts;
        List<AccountResponse> accountResponseList = new ArrayList<>();

        logger.info("Find All Started");

        Pageable paging = PageRequest.of(page, size);

        Page<Account> pageAccs;

        pageAccs = accountRepository.findAll(paging);

        accounts = pageAccs.getContent();
        
        for(Account acc : accounts) {
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.setAmount(acc.getAmount());
            accountResponse.setAccountId(acc.getAccountId());
            accountResponse.setUserid(acc.getUserInfo().getUserId());
            accountResponse.setAccountNumber(acc.getAccountNumber());
            accountResponseList.add(accountResponse);
        }


        findAllAccountsResponse.setAccounts(accountResponseList);
        findAllAccountsResponse.setCurrentPage(pageAccs.getNumber());
        findAllAccountsResponse.setTotalItems(pageAccs.getTotalElements());
        findAllAccountsResponse.setTotalPages(pageAccs.getTotalPages());

        ResponseEntity<FindAllAccountsResponse> response = new ResponseEntity<>(findAllAccountsResponse, HttpStatus.OK);
        logger.info("Response {}", response);
        return response;
    }

    public ResponseEntity<AccountResponse> createAccount(Account account,String username) throws AlreadyHaveAccountException {
        AccountResponse accountResponse = new AccountResponse();


        logger.info("Create Account Started");

        logger.info("Username : " + username);
        UserInfo userInfo = userInfoRepository.getUserInfoByName(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (userInfo.getAccount() != null) {
            logger.info("Create Account failed, have already an account");
            throw new AlreadyHaveAccountException();
        }

        account.setUserInfo(userInfo);
        Random rand = new Random();
        String card = "TR";
        for (int i = 0; i < 14; i++) {
            int n = rand.nextInt(10);
            card += Integer.toString(n);
        }
        account.setAccountNumber(card);
        Account account1 = accountRepository.save(account);
        accountResponse.setAccountId(account1.getAccountId());
        accountResponse.setAmount(account1.getAmount());
        accountResponse.setUserid(account1.getUserInfo().getUserId());
        accountResponse.setAccountNumber(account1.getAccountNumber());


        logger.info("Create Account finished successfully.");
        logger.info("Response {}", accountResponse);
        return new ResponseEntity<AccountResponse>(accountResponse,HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<MoneyTransactionResponse> sendMoneyToAccount(String accountNumber, String username, int amount){

        logger.info("Add money account started");

        
        
        MoneyTransactionResponse sendResponse = new MoneyTransactionResponse();
        sendResponse.setTransactionName(Response.TRANSFER_MONEY);

        Account accountOfReceiver = accountRepository.getAccountByAccountNumber(accountNumber).orElseThrow(() -> new AccountwithAccountNumberNotFoundException(accountNumber));

        UserInfo userInfo = userInfoRepository.getUserInfoByName(username).orElseThrow(() -> new UsernameNotFoundException("Username with "+ username + "not found"));

        Account accountOfSender = accountRepository.getAccountByUserInfo(userInfo).orElseThrow(() -> new AccountNotFoundException("Account not found."));

        if(accountOfReceiver.getAccountNumber().equals(accountOfSender.getAccountNumber())) {
            sendResponse.setResponseStatus(Response.FAIL);
            sendResponse.setCurrentAmount(accountOfSender.getAmount());
            sendResponse.setMessage("You can not send money to your account.");
            logger.info("Response {}", sendResponse);
            return new ResponseEntity<>(sendResponse,HttpStatus.BAD_REQUEST);
        }

        if(accountOfSender.getAmount() < amount) {
            sendResponse.setResponseStatus(Response.FAIL);
            sendResponse.setCurrentAmount(accountOfSender.getAmount());
            sendResponse.setMessage("You do not have enough money in your account");
            logger.info("Response {}", sendResponse);
            return new ResponseEntity<>(sendResponse,HttpStatus.BAD_REQUEST);
        }

        accountOfSender.setAmount(accountOfSender.getAmount() - amount);
        accountOfReceiver.setAmount(accountOfReceiver.getAmount() + amount);
        accountRepository.save(accountOfSender);
        accountRepository.save(accountOfReceiver);

        sendResponse.setCurrentAmount(accountOfSender.getAmount());
        sendResponse.setResponseStatus(Response.SUCCESS);
        sendResponse.setMessage("You transferred your money successfully.");

        logger.info("Response {}", sendResponse);
        logger.info("Send money account finished successfully.");

        return new ResponseEntity<>(sendResponse,HttpStatus.OK);
    }

    public ResponseEntity<MoneyTransactionResponse> withdrawMoney(String username, int amount) {
    }

    public ResponseEntity<MoneyTransactionResponse> depositMoney(String username, int amount) {
    }
}
