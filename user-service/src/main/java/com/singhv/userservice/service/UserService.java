package com.singhv.userservice.service;

import com.singhv.userservice.dto.LoginRequestDTO;
import com.singhv.userservice.dto.LoginResponseDTO;
import com.singhv.userservice.dto.SignUpResponseDTO;
import com.singhv.userservice.dto.UserInfoDTO;

public interface UserService {
    UserInfoDTO getUserInfo(String userId);
    SignUpResponseDTO registerNewUser(UserInfoDTO userInfo);
    LoginResponseDTO checkUserLogin(LoginRequestDTO requestContent);
}