package com.singhv.CarSharingTZ.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Vehicles {
    @NotBlank
    private String userId;

    @Id
    @NotBlank
    private String vehicleNumber;

    @NotBlank
    private String vehicleType;

    private String vehicleName;
    private String vehicleColor;
}