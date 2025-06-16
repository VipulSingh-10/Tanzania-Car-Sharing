package com.singhv.CarSharingTZ.dto;

import com.singhv.CarSharingTZ.models.Points;
import lombok.Data;

import java.util.Date;

@Data
public class CreateTripResponseDTO {
    private String tripId;
    private String vehicleNumber;
    private Points pickupPoint;
    private Points destinationPoint;
    private Date tripStartTime;
    private boolean tripCreated;
    private String errMsg;
}
