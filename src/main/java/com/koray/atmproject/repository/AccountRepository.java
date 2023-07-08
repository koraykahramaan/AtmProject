package com.koray.atmproject.repository;

import com.koray.atmproject.model.Account;
import com.koray.atmproject.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {

    // select * from accounts a join users u on u.id = a.userid;
//    @Query(value = "SELECT DISTINCT a from Account a join UserInfo u on u.userId = a.userInfo.userId")
    Page<Account> findAll(Pageable pageable);

    Optional<Account> getAccountByAccountNumber(String accountNumber);

    Optional<Account> getAccountByUserInfo(UserInfo userInfo);
    // get account from account number

}
