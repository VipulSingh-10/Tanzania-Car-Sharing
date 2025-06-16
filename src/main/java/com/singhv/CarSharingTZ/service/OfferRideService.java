package com.singhv.CarSharingTZ.service;

import com.singhv.CarSharingTZ.dto.CreateTripResponseDTO;
import com.singhv.CarSharingTZ.dto.OfferRideDTO;
import com.google.maps.errors.ApiException;

import java.io.IOException;

public interface OfferRideService {
    CreateTripResponseDTO createTrip(String userId, OfferRideDTO requestContent) throws InterruptedException, ApiException, IOException;
}
