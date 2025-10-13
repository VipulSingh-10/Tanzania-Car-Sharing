package com.singhv.tripservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for finding available rides
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindRideRequestDTO {

    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupLocation;
    private Double pickupRadiusKm;

    private Double dropoffLatitude;
    private Double dropoffLongitude;
    private String dropoffLocation;
    private Double dropoffRadiusKm;

    private LocalDateTime departureTime;
    private Integer numberOfPassengers;
}

