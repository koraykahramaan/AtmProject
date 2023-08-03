package com.koray.atmproject.service;


import com.koray.atmproject.dto.AccountResponse;
import com.koray.atmproject.dto.FindAllAccountsResponse;
import com.koray.atmproject.dto.KafkaMessage;
import com.koray.atmproject.dto.MoneyTransactionResponse;
import com.koray.atmproject.exception.AccountNotFoundException;
import com.koray.atmproject.exception.AccountwithAccountNumberNotFoundException;
import com.koray.atmproject.exception.AlreadyHaveAccountException;
import com.koray.atmproject.model.Account;
import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.repository.AccountRepository;
import com.koray.atmproject.repository.UserInfoRepository;
import com.koray.atmproject.util.AccountMapper;
import com.koray.atmproject.util.Response;
import com.koray.atmproject.util.TransactionTypes;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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
    @Autowired
    KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    @Autowired
    KafkaService kafkaService;


    Random rand = new Random();
    AccountMapper accountMapper = new AccountMapper();

    String logResponse = "Response : {}";

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public ResponseEntity<AccountResponse> getAccountById(int id) {

        logger.info("Get Account By Id Started with id : {}", id);

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
        AccountResponse accountResponse = accountMapper.accountToAccountResponseMapper(account);
        accountResponse.setTransaction(TransactionTypes.GET_ACCOUNT);

        logger.info(logResponse, accountResponse);
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }


    public ResponseEntity<FindAllAccountsResponse> findAll(int page, int size) {

        FindAllAccountsResponse findAllAccountsResponse = new FindAllAccountsResponse();
        findAllAccountsResponse.setTransaction(TransactionTypes.FIND_ALL);
        List<Account> accounts;
        List<AccountResponse> accountResponseList = new ArrayList<>();

        logger.info("Find All Started");

        Pageable paging = PageRequest.of(page, size);

        Page<Account> pageAccs;

        pageAccs = accountRepository.findAll(paging);

        accounts = pageAccs.getContent();
        
        for(Account acc : accounts) {

            AccountResponse accountResponse = accountMapper.accountToAccountResponseMapper(acc);
            accountResponse.setTransaction(TransactionTypes.FIND_ALL);
            accountResponseList.add(accountResponse);
        }

        findAllAccountsResponse.setAccounts(accountResponseList);
        findAllAccountsResponse.setCurrentPage(pageAccs.getNumber());
        findAllAccountsResponse.setTotalItems(pageAccs.getTotalElements());
        findAllAccountsResponse.setTotalPages(pageAccs.getTotalPages());

        ResponseEntity<FindAllAccountsResponse> response = new ResponseEntity<>(findAllAccountsResponse, HttpStatus.OK);
        logger.info(logResponse, response);
        return response;
    }

    public ResponseEntity<AccountResponse> createAccount(Account account,String username) throws AlreadyHaveAccountException {

        logger.info("Create Account Started");

        logger.info("Username : {}", username);
        UserInfo userInfo = userInfoRepository.getUserInfoByName(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (userInfo.getAccount() != null) {
            logger.info("Create Account failed, have already an account");
            throw new AlreadyHaveAccountException();
        }

        account.setUserInfo(userInfo);
        StringBuilder stringBuilder = new StringBuilder("TR");
        for (int i = 0; i < 14; i++) {
            int n = rand.nextInt(10);
            stringBuilder.append(n);
        }
        String card = stringBuilder.toString();
        account.setAccountNumber(card);
        Account account1 = accountRepository.save(account);
        AccountResponse accountResponse = accountMapper.accountToAccountResponseMapper(account1);
        accountResponse.setTransaction(TransactionTypes.CREATE_ACCOUNT);


        logger.info("Create Account finished successfully.");
        logger.info(logResponse, accountResponse); //
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<MoneyTransactionResponse> sendMoneyToAccount(String accountNumber, String username, int amount){

        logger.info("Add money account started");

        MoneyTransactionResponse sendResponse = new MoneyTransactionResponse();
        sendResponse.setTransactionName(TransactionTypes.TRANSFER_MONEY);

        Account accountOfReceiver = accountRepository.getAccountByAccountNumber(accountNumber).orElseThrow(() -> new AccountwithAccountNumberNotFoundException(accountNumber));

        Account accountOfSender = getAccountFromUserName(username);

        if(accountOfReceiver.getAccountNumber().equals(accountOfSender.getAccountNumber())) {
            sendResponse = accountMapper.accountToMoneyTransactionResponseMapper(sendResponse,accountOfSender,Response.FAIL,"You can not send money to your account.");
            logger.info(logResponse, sendResponse);
            return new ResponseEntity<>(sendResponse,HttpStatus.BAD_REQUEST);
        }

        if(accountOfSender.getAmount() < amount) {
            sendResponse = accountMapper.accountToMoneyTransactionResponseMapper(sendResponse,accountOfSender,Response.FAIL,"You do not have enough money in your account");
            logger.info(logResponse, sendResponse);
            return new ResponseEntity<>(sendResponse,HttpStatus.BAD_REQUEST);
        }

        accountOfSender.setAmount(accountOfSender.getAmount() - amount);
        accountOfReceiver.setAmount(accountOfReceiver.getAmount() + amount);
        accountRepository.save(accountOfSender);
        accountRepository.save(accountOfReceiver);
        KafkaMessage kafkaMessage = kafkaService.getKafkaMessageFromAccounts(accountOfSender,accountOfReceiver,amount);
        logger.info("Before kafka message sending");
        try {
            kafkaTemplate.send("money_transfer",kafkaMessage);
        }
        catch (Exception e) {
            logger.info("Error during kafka message send. {}", e.getMessage());
        }
        sendResponse = accountMapper.accountToMoneyTransactionResponseMapper(sendResponse,accountOfSender,Response.SUCCESS,"You transferred your money successfully.");

        logger.info(logResponse, sendResponse);
        logger.info("Send money account finished successfully.");

        return new ResponseEntity<>(sendResponse,HttpStatus.OK);
    }

    public ResponseEntity<MoneyTransactionResponse> withdrawMoney(String username, int amount) {
        logger.info("Withdrawing money started");

        MoneyTransactionResponse withdrawMoneyResponse = new MoneyTransactionResponse();
        withdrawMoneyResponse.setTransactionName(TransactionTypes.WITHDRAW_MONEY);

        Account account = getAccountFromUserName(username);

        if(account.getAmount() < amount) {
            withdrawMoneyResponse = accountMapper.accountToMoneyTransactionResponseMapper(withdrawMoneyResponse,account,Response.FAIL,"You do not have enough money in your account");
            logger.info(logResponse,withdrawMoneyResponse);
            return new ResponseEntity<>(withdrawMoneyResponse,HttpStatus.BAD_REQUEST);
        }

        account.setAmount(account.getAmount() - amount);
        accountRepository.save(account);

        withdrawMoneyResponse = accountMapper.accountToMoneyTransactionResponseMapper(withdrawMoneyResponse,account,Response.SUCCESS,"You withdrawed your money successfuly.");

        logger.info(logResponse,withdrawMoneyResponse);
        logger.info("Withdraw money finished");

        return new ResponseEntity<>(withdrawMoneyResponse,HttpStatus.OK);
    }

    public ResponseEntity<MoneyTransactionResponse> depositMoney(String username, int amount) {
        logger.info("Deposit money started");

        Account account = getAccountFromUserName(username);
        account.setAmount(account.getAmount() + amount);
        accountRepository.save(account);

        MoneyTransactionResponse depositMoneyResponse = accountMapper.accountToMoneyTransactionResponseMapper(account, Response.SUCCESS,"Deposit money transaction completed successfully");
        depositMoneyResponse.setTransactionName(TransactionTypes.DEPOSIT_MONEY);

        logger.info(logResponse,depositMoneyResponse);
        logger.info("Deposit money transaction completed successfully");

        return new ResponseEntity<>(depositMoneyResponse,HttpStatus.OK);
    }

    public ResponseEntity<AccountResponse> deleteAccount(String username) {
        logger.info("Delete Account started");

        AccountResponse deleteAccountResponse = new AccountResponse();
        deleteAccountResponse.setTransaction(TransactionTypes.DELETE_ACCOUNT);
        Account account = getAccountFromUserName(username);
        accountRepository.delete(account);

        deleteAccountResponse.setAccountNumber("");
        deleteAccountResponse.setAccountId(-1);
        deleteAccountResponse.setAmount(-1);
        deleteAccountResponse.setUserid(getUserIdFromUserName(username));

        logger.info(logResponse,deleteAccountResponse);
        logger.info("Delete Account completed successfully completed");

        return new ResponseEntity<>(deleteAccountResponse,HttpStatus.OK);
    }

    public Account getAccountFromUserName(String username) {
        UserInfo userInfo = userInfoRepository.getUserInfoByName(username).orElseThrow(() -> new UsernameNotFoundException("Username with " + username + " is not found"));
        return accountRepository.getAccountByUserInfo(userInfo).orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    public int getUserIdFromUserName(String username) {
        UserInfo userInfo = userInfoRepository.getUserInfoByName(username).orElseThrow(() -> new UsernameNotFoundException("Username with " + username + " is not found"));
        return userInfo.getUserId();
    }
}
