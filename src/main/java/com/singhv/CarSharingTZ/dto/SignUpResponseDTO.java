package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class SignUpResponseDTO {
    private String userId;
    private String username;
    private boolean signUpSuccess;
}
