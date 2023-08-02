package com.koray.atmproject.controller;

import com.koray.atmproject.dto.AuthRequest;
import com.koray.atmproject.dto.UserInfoResponse;
import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.service.JWTService;
import com.koray.atmproject.service.UserInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Users")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/register")
    public UserInfoResponse register(@RequestBody @Valid UserInfo userInfo) {
        return userInfoService.register(userInfo);
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));

        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        }

        else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }


}
