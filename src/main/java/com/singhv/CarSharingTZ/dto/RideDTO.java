package com.singhv.CarSharingTZ.dto;

import com.singhv.CarSharingTZ.models.Points;
import lombok.Data;

import java.util.Date;

@Data
public class RideDTO {
    // userId of the Trip Owner
    private String userId;
    private String tripId;
    private Points pickupPoint;
    private Points destinationPoint;
    private Date rideStartTime;
    private int requestedSeats;
    private String tripStatus;
    private String rideStatus;
}
