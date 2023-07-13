package com.koray.atmproject.service;

import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private final Logger logger = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(UserInfo userInfo) {

        logger.info("add user method");

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        if(userInfo.getRoles().isEmpty()) {
            userInfo.setRoles("ROLE_USER");
        }
        userInfoRepository.save(userInfo);
        return "New user added to database";
    }
}
