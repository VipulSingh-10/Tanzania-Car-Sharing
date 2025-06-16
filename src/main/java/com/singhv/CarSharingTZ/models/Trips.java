package com.singhv.CarSharingTZ.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class Trips {
    @Id
    private String tripId;

    @NotBlank
    private String tripStatus;

    @NotBlank
    private String userId;

    @NotBlank
    private String vehicleNumber;

    @NotNull
    private Points pickupPoint;

    @NotNull
    private Points destinationPoint;

    @NotNull
    private Date tripStartTime;

    private Date tripEndTime;

    @Min(1)
    private int offeredSeats;

    @Min(0)
    private int currSeats;

    @PositiveOrZero
    private double pricePerKm;

    private List<ObjectId> joinedRidersId;
    private Date createdDate = new Date();
    private String createdBy;
}