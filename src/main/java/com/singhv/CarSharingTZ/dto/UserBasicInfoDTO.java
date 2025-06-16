package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class UserBasicInfoDTO {
    private String userId;
    private String fullName;
    private String phoneNumber;
    private String organisationName;
    private String profilePicUrl;
}
