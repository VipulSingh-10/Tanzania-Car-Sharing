package com.singhv.CarSharingTZ.dto;

import com.singhv.CarSharingTZ.models.Points;
import lombok.Data;

import java.util.Date;

@Data
public class TripBasicInfoDTO {
    private String userId;
    private String tripId;
    private String profilePic;
    private String fullName;
    private String vehicleNumber;
    private Points pickupPoint;
    private Points destinationPoint;
    private Date tripStartTime;
    private int availableSeats;
    private String phoneNumber;
    private int requestedSeats;
}
