package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class CancelRideRequestDTO {
    private String tripId;
    private String cancellationReason; // Optional field to collect reason
}