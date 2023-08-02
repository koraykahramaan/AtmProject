package com.koray.atmproject.service;

import com.koray.atmproject.dto.UserInfoResponse;
import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.repository.UserInfoRepository;
import com.koray.atmproject.util.UserInfoMapper;
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

    UserInfoMapper userInfoMapper = new UserInfoMapper();

    public UserInfoResponse register(UserInfo userInfo) {

        logger.info("add user started");

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        if(userInfo.getRoles().isEmpty()) {
            userInfo.setRoles("ROLE_USER");
        }
        UserInfo savedUser = userInfoRepository.save(userInfo);
        logger.info("add user ended");
        return userInfoMapper.userInfoResponseToUserInfo(savedUser);
    }
}
