package com.singhv.CarSharingTZ.service;

import com.singhv.CarSharingTZ.dto.JoinRideResponseDTO;
import com.singhv.CarSharingTZ.dto.RideDTO;
import com.singhv.CarSharingTZ.dto.TripBasicInfoDTO;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface FindRideService {

    List<TripBasicInfoDTO> getBestMatchingRide(String userId, RideDTO requestContent) throws ParseException;

    JoinRideResponseDTO insertRideToTrip(String riderUserId, RideDTO requestContent) throws InterruptedException, ApiException, IOException;
}
