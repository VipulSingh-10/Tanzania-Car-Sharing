package com.singhv.CarSharingTZ.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.singhv.CarSharingTZ.models.Points;
import lombok.Data;

import java.util.Date;

@Data
public class OfferRideDTO {

    private String vehicleNumber;
    private Points pickupPoint;
    private Points destinationPoint;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")

    private Date tripStartTime;
    private int offeredSeats;

    @Override
    public String toString() {
        return "OfferRideDTO{" +
                "carNumber='" + vehicleNumber + '\'' +
                ", pickupPoint=" + pickupPoint +
                ", destinationPoint=" + destinationPoint +
                ", tripStartTime=" + tripStartTime +
                ", offeredSeats=" + offeredSeats +
                '}';
    }
}
