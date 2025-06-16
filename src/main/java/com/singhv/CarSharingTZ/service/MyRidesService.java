package com.singhv.CarSharingTZ.service;

import com.singhv.CarSharingTZ.dto.CancelRideRequestDTO;
import com.singhv.CarSharingTZ.dto.CancelRideResponseDTO;
import com.singhv.CarSharingTZ.dto.RideBasicInfoDTO;

import java.util.List;

public interface MyRidesService {

    List<RideBasicInfoDTO> getUpcomingRides(String riderUserId);

    List<RideBasicInfoDTO> getHistoryRides(String rideUserId);
    CancelRideResponseDTO cancelRide(String userId, CancelRideRequestDTO requestContent);
}
