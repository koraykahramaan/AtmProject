package com.koray.atmproject.repository;

import com.koray.atmproject.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {
    Optional<UserInfo> findByName(String username);
    Optional<UserInfo> getUserInfoByName(String username);
}
