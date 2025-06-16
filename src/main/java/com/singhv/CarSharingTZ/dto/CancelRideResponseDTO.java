package com.singhv.CarSharingTZ.dto;

import lombok.Data;

@Data
public class CancelRideResponseDTO {
    private boolean rideCancelled;
    private String errMsg;
}