package com.singhv.CarSharingTZ.service.implementation;

import com.singhv.CarSharingTZ.dto.CreateTripResponseDTO;
import com.singhv.CarSharingTZ.dto.OfferRideDTO;
import com.singhv.CarSharingTZ.models.Riders;
import com.singhv.CarSharingTZ.models.Trips;
import com.singhv.CarSharingTZ.models.enums.RideStatusEnum;
import com.singhv.CarSharingTZ.models.enums.TripStatusEnum;
import com.singhv.CarSharingTZ.repository.RideRepository;
import com.singhv.CarSharingTZ.repository.TripsRepository;
import com.singhv.CarSharingTZ.service.OfferRideService;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OfferRideServiceImplementation implements OfferRideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private GeoApiContext geoApiContext;

    @Override
    public CreateTripResponseDTO createTrip(String userId, OfferRideDTO requestContent) throws InterruptedException, ApiException, IOException {
        // Check if a trip already exists for the given user and time
        if (tripsRepository.findByUserIdAndTripStartTime(userId, requestContent.getTripStartTime()) != null) {
            CreateTripResponseDTO responseDTO = new CreateTripResponseDTO();
            responseDTO.setTripCreated(false);
            responseDTO.setErrMsg("A Trip Already Exists for selected Time");
            return responseDTO;
        }

        // Resolve pickup point address if not provided
        if (requestContent.getPickupPoint().getPlaceAddress() == null || requestContent.getPickupPoint().getPlaceAddress().isEmpty()) {
            LatLng pickupAddressLatLng = new LatLng(requestContent.getPickupPoint().getLatitude(), requestContent.getPickupPoint().getLongitude());
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, pickupAddressLatLng).await();
            if (results.length > 0) {
                requestContent.getPickupPoint().setPlaceAddress(results[0].formattedAddress);
            }
        }

        // Resolve destination point address if not provided
        if (requestContent.getDestinationPoint().getPlaceAddress() == null || requestContent.getDestinationPoint().getPlaceAddress().isEmpty()) {
            LatLng destinationAddressLatLng = new LatLng(requestContent.getDestinationPoint().getLatitude(), requestContent.getDestinationPoint().getLongitude());
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, destinationAddressLatLng).await();
            if (results.length > 0) {
                requestContent.getDestinationPoint().setPlaceAddress(results[0].formattedAddress);
            }
        }

        // Handle tripStartTime (String or Date)
        Object tripStartTimeRaw = requestContent.getTripStartTime();

        ZonedDateTime tripStartTime;
        if (tripStartTimeRaw instanceof String) {
            // Handle String case (ISO-8601 format expected)
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
            tripStartTime = ZonedDateTime.parse((String) tripStartTimeRaw, formatter);
        } else if (tripStartTimeRaw instanceof Date) {
            // Handle Date case
            tripStartTime = ((Date) tripStartTimeRaw).toInstant().atZone(ZoneId.of("UTC"));
        } else {
            throw new IllegalArgumentException("Invalid tripStartTime format: Must be String or Date");
        }

        // If needed, adjust to the system's default timezone
        ZonedDateTime systemTime = tripStartTime.withZoneSameInstant(ZoneId.systemDefault());

        // Create a new Trip object and populate values
        Trips newTrip = new Trips();
        BeanUtils.copyProperties(requestContent, newTrip);
        newTrip.setUserId(userId);
        newTrip.setTripStatus(TripStatusEnum.ACTIVE_STATUS);
        newTrip.setCreatedDate(new Date());
        newTrip.setCreatedBy(userId);
        newTrip.setCurrSeats(0);
        newTrip.setTripStartTime(Date.from(systemTime.toInstant())); // Ensure proper timezone conversion
        log.info("CreateTripRequest-->" + newTrip.toString());

        // Save the new trip to the repository
        tripsRepository.save(newTrip);

        // Fetch the trip just created
        Trips trip = tripsRepository.findByUserIdAndTripStartTime(userId, requestContent.getTripStartTime());

        // Create and save a new Rider object for the created trip
        Riders rider = new Riders();
        BeanUtils.copyProperties(trip, rider);
        rider.setAllottedTripId(trip.getTripId());
        rider.setRequestedSeats(0);
        rider.setRideStatus(RideStatusEnum.ALLOTTED_STATUS);
        rider.setRideStartTime(trip.getTripStartTime());
        rider.setRiderTripOwner(true);
        rideRepository.save(rider);

        // Update trip with a list of joined riders
        Riders currRide = rideRepository.findByUserIdAndAllottedTripId(userId, trip.getTripId());
        List<ObjectId> tripRiders = new ArrayList<>();
        tripRiders.add(new ObjectId(currRide.getRideId()));
        trip.setJoinedRidersId(tripRiders);
        tripsRepository.save(trip);

        // Prepare and return the response
        CreateTripResponseDTO responseDTO = new CreateTripResponseDTO();
        BeanUtils.copyProperties(newTrip, responseDTO);
        responseDTO.setTripCreated(true);
        return responseDTO;
    }
}