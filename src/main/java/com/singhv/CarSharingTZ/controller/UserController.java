package com.singhv.CarSharingTZ.controller;

import com.singhv.CarSharingTZ.dto.*;
import com.singhv.CarSharingTZ.helper.ErrorMessages;
import com.singhv.CarSharingTZ.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Get User Profile
 * Get User Basic Profile
 * Register User Cars
 * Get User Cars
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get User Info
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/{userId}")
    public ResponseDTO<UserInfoDTO> getUserInfo(@PathVariable String userId) {
        log.info("Get user info for userId: {}", userId);
        ResponseDTO<UserInfoDTO> response = new ResponseDTO<>();

        try {
            if (verifyUser(userId)) {
                UserInfoDTO userInfo = userService.getUserInfo(userId);
                response.setResponseContent(userInfo);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(null);
                response.setSuccess(false);
                response.setErrorMessage(ErrorMessages.SOME_UNEXPECTED_ERROR_OCCUR);
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }

    /**
     * Add New User
     *
     * @param requestDTO
     * @return
     */
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<SignUpResponseDTO> addNewUser(@RequestBody RequestDTO<UserInfoDTO> requestDTO) {
        log.info("Signup request: {}", requestDTO);
        ResponseDTO<SignUpResponseDTO> response = new ResponseDTO<>();

        try {
            SignUpResponseDTO signUpResponse = userService.registerNewUser(requestDTO.getRequestContent());
            if (signUpResponse.isSignUpSuccess()) {
                response.setResponseContent(signUpResponse);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(null);
                response.setSuccess(false);
                response.setErrorMessage(null);//need to change later
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }

    /**
     * Check User Login
     *
     * @param loginInfo
     * @return
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<LoginResponseDTO> loginUser(@RequestBody RequestDTO<LoginRequestDTO> loginInfo) {
        //log.info("Login request for: {}", loginInfo.getRequestContent().getEmail());
        ResponseDTO<LoginResponseDTO> response = new ResponseDTO<>();

        try {
            LoginResponseDTO loginResponse = userService.checkUserLogin(loginInfo.getRequestContent());
            if (loginResponse.isLoginSuccess()) {
                response.setResponseContent(loginResponse);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(null);
                response.setSuccess(false);
                response.setErrorMessage(loginResponse.getErrMsg());
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }

    private boolean verifyUser(String userId) {
        return true;
    }
}