package com.koray.atmproject.util;

import com.koray.atmproject.dto.UserInfoResponse;
import com.koray.atmproject.model.UserInfo;

public class UserInfoMapper {

    public UserInfoResponse userInfoResponseToUserInfo(UserInfo userInfo) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setName(userInfo.getName());
        userInfoResponse.setEmail(userInfo.getEmail());
        userInfoResponse.setRoles(userInfo.getRoles());
        return userInfoResponse;
    }
}
