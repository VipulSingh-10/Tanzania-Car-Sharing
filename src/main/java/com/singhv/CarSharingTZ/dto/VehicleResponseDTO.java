package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class VehicleResponseDTO {
    // Vehicle Number
    private String value;
    // vehicle name + color
    private String text;
    private String seatingCapacity;
}
