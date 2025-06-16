package com.singhv.CarSharingTZ.dto;

import com.singhv.CarSharingTZ.models.Vehicles;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserInfoDTO {
    private String fullName;
    private String emailId;
    private String userId = emailId;
    private String phoneNumber;
    private String password;
    private int age;
    private Date dob;
    private String empId;
    private String organisationName;
    private String profilePicUrl;
    private List<Vehicles> userCars;
}
//changes expected
//@Data
//public class UserInfoDTO {
//    private String fullName;
//    private String emailId;
//    private String userId;  // Remove initialization
//    private String phoneNumber;
//    // Other fields...
//    private List<VehicleResponseDTO> userCars;  // Use a DTO instead of domain model
//}