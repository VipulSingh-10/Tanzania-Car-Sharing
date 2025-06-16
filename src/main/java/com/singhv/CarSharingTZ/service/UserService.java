package com.singhv.CarSharingTZ.service;

import com.singhv.CarSharingTZ.dto.LoginRequestDTO;
import com.singhv.CarSharingTZ.dto.LoginResponseDTO;
import com.singhv.CarSharingTZ.dto.SignUpResponseDTO;
import com.singhv.CarSharingTZ.dto.UserInfoDTO;

import java.util.concurrent.ExecutionException;

public interface UserService {

    UserInfoDTO getUserInfo(String userId) throws ExecutionException, InterruptedException;

    SignUpResponseDTO registerNewUser(UserInfoDTO userInfo);

    LoginResponseDTO checkUserLogin(LoginRequestDTO requestContent);
}
