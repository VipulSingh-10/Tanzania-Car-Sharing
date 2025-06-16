package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String username;
    private String userId;
    private boolean loginSuccess;
    private String errMsg;
}
