package com.singhv.CarSharingTZ.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Riders {
    @Id
    private String rideId;

    @NotBlank
    private String userId;

    private String allottedTripId;

    @NotBlank
    private String rideStatus;

    @NotNull
    private Points pickupPoint;

    @NotNull
    private Points destinationPoint;

    private Date rideStartTime;
    private Date rideEndTime;
    private double rideDistance;

    @Min(1)
    private int requestedSeats = 1;

    private Date createdDate = new Date();
    private String createdBy;
    private boolean isRiderTripOwner;
}