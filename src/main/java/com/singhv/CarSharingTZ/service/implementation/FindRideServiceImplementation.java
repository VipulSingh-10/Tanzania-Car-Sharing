package com.singhv.CarSharingTZ.service.implementation;

import com.singhv.CarSharingTZ.dto.JoinRideResponseDTO;
import com.singhv.CarSharingTZ.dto.RideDTO;
import com.singhv.CarSharingTZ.dto.TripBasicInfoDTO;
import com.singhv.CarSharingTZ.helper.DateHelper;
import com.singhv.CarSharingTZ.helper.DirectionsHelper;
import com.singhv.CarSharingTZ.helper.ErrorMessages;
import com.singhv.CarSharingTZ.models.Points;
import com.singhv.CarSharingTZ.models.Riders;
import com.singhv.CarSharingTZ.models.Trips;
import com.singhv.CarSharingTZ.models.User;
import com.singhv.CarSharingTZ.models.enums.RideStatusEnum;
import com.singhv.CarSharingTZ.models.enums.TripStatusEnum;
import com.singhv.CarSharingTZ.repository.RideRepository;
import com.singhv.CarSharingTZ.repository.TripsRepository;
import com.singhv.CarSharingTZ.repository.UserRepository;
import com.singhv.CarSharingTZ.service.FindRideService;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FindRideServiceImplementation implements FindRideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeoApiContext geoApiContext;

    @Autowired
    private DirectionsHelper directionsHelper;

    /**
     * Allot user chosen ride to trip
     *
     * @param riderUserId
     * @param requestContent
     * @return
     */
    @Override
    public JoinRideResponseDTO insertRideToTrip(String riderUserId, RideDTO requestContent) throws InterruptedException, ApiException, IOException {
        JoinRideResponseDTO responseDTO = new JoinRideResponseDTO();
        if (rideRepository.findByUserIdAndAllottedTripId(riderUserId, requestContent.getTripId()) != null) {
            responseDTO.setRideJoined(false);
            responseDTO.setErrMsg(ErrorMessages.RIDER_ALREADY_JOINED);
            return responseDTO;
        }
        Trips trip = tripsRepository.findByTripId(requestContent.getTripId());
        if (trip.getTripStatus().equalsIgnoreCase(TripStatusEnum.COMPLETED_STATUS) || trip.getTripStatus().equalsIgnoreCase(TripStatusEnum.CANCELLED_STATUS)) {
            responseDTO.setRideJoined(false);
            responseDTO.setErrMsg(ErrorMessages.TRIP_ALREADY_ENDED);
            return responseDTO;
        }
        if (trip.getTripStatus().equalsIgnoreCase(TripStatusEnum.FILLED_STATUS) || trip.getCurrSeats() == trip.getOfferedSeats()) {
            responseDTO.setRideJoined(false);
            responseDTO.setErrMsg(ErrorMessages.NO_SEATS_AVAILABLE_IN_TRIP);
            return responseDTO;
        }
        if ((trip.getOfferedSeats() - trip.getCurrSeats()) < requestContent.getRequestedSeats()) {
            responseDTO.setRideJoined(false);
            responseDTO.setErrMsg(ErrorMessages.NO_SEATS_AVAILABLE_IN_TRIP);
            return responseDTO;
        }
        GeocodingResult[] results;
        if (requestContent.getPickupPoint().getPlaceAddress().isEmpty() || requestContent.getPickupPoint().getPlaceAddress() == null) {
            LatLng pickupAddressLatLng = new LatLng(requestContent.getPickupPoint().getLatitude(), requestContent.getPickupPoint().getLongitude());
            results = GeocodingApi.reverseGeocode(geoApiContext, pickupAddressLatLng).await();
            if (results.length > 0) {
                requestContent.getPickupPoint().setPlaceAddress(results[0].formattedAddress);
            }
        }

        if (requestContent.getDestinationPoint().getPlaceAddress().isEmpty() || requestContent.getDestinationPoint().getPlaceAddress() == null) {
            LatLng destinationAddressLatLng = new LatLng(requestContent.getDestinationPoint().getLatitude(), requestContent.getDestinationPoint().getLongitude());
            results = GeocodingApi.reverseGeocode(geoApiContext, destinationAddressLatLng).await();
            if (results.length > 0) {
                requestContent.getDestinationPoint().setPlaceAddress(results[0].formattedAddress);
            }
        }
        Riders rider = new Riders();
        BeanUtils.copyProperties(requestContent, rider);
        rider.setUserId(riderUserId);
        rider.setAllottedTripId(requestContent.getTripId());
        rider.setRideStatus(RideStatusEnum.ALLOTTED_STATUS);
        rider.setRequestedSeats(requestContent.getRequestedSeats());
        rider.setCreatedBy(riderUserId);
        rideRepository.save(rider);
        rider = rideRepository.findByUserIdAndAllottedTripId(riderUserId, requestContent.getTripId());

        // Increase CurrSeats count
        // if all seats are filled then change status
        trip.getJoinedRidersId().add(new ObjectId(rider.getRideId()));
        trip.setCurrSeats(trip.getCurrSeats() + requestContent.getRequestedSeats());
        if (trip.getCurrSeats() == trip.getOfferedSeats()) {
            trip.setTripStatus(TripStatusEnum.FILLED_STATUS);
        }
        tripsRepository.save(trip);


        responseDTO.setRideJoined(true);
        responseDTO.setErrMsg(null);

        return responseDTO;
    }

    /**
     * Find the best suited trip options for the rider
     * first sort by smallest distance and the by time
     *
     * @param requestContent
     * @return
     */
    @Override
    public List<TripBasicInfoDTO> getBestMatchingRide(String userId, RideDTO requestContent) throws ParseException {
        // Extract the rider's pickup and destination points from the request
        Points riderPickupPoint = requestContent.getPickupPoint();
        Points riderDestinationPoint = requestContent.getDestinationPoint();

        // Fetch all active trips from the repository
        List<Trips> activeTrips = tripsRepository.findByTripStatus(TripStatusEnum.ACTIVE_STATUS);

        // Filter trips based on seat availability, proximity, and better matching logic
        List<Trips> matchingTrips = activeTrips.stream()
                .filter(trip -> trip.getCurrSeats() < trip.getOfferedSeats()) // Check trips with available seats
                .filter(trip -> directionsHelper.isPickupNearBy(trip.getPickupPoint(), riderPickupPoint)) // Check nearby pickup point
                .filter(trip -> directionsHelper.isPickupNearBy(trip.getDestinationPoint(), riderDestinationPoint)) // Check nearby destination point
                .collect(Collectors.toList());

        // Sort the filtered trips by better matching criteria
        List<Trips> sortedTrips = matchingTrips.stream()
                .sorted(Comparator.comparing((Trips trip) ->
                                directionsHelper.getDistanceFromAPointFromPolyline(trip.getPickupPoint(), trip.getDestinationPoint(), riderPickupPoint)) // Sort by rider's distance to pickup point
                        .thenComparing((Trips trip) ->
                                directionsHelper.getDistanceFromAPointFromPolyline(trip.getPickupPoint(), trip.getDestinationPoint(), riderDestinationPoint)) // Sort by rider's distance to destination point
                        .thenComparing(Trips::getTripStartTime)) // Sort by earliest trip start time
                .collect(Collectors.toList());

        // Map sorted trips to TripBasicInfoDTO to form the response
        List<TripBasicInfoDTO> response = sortedTrips.stream()
                .map(trip -> {
                    TripBasicInfoDTO dto = new TripBasicInfoDTO();
                    dto.setUserId(trip.getUserId());
                    dto.setTripId(trip.getTripId());
                    dto.setPickupPoint(trip.getPickupPoint());
                    dto.setDestinationPoint(trip.getDestinationPoint());
                    dto.setTripStartTime(trip.getTripStartTime());
                    dto.setAvailableSeats(trip.getOfferedSeats() - trip.getCurrSeats());
                    dto.setVehicleNumber(trip.getVehicleNumber());

                    // Add driver/user information (fullName, profilePic, phoneNumber)
                    userRepository.findById(trip.getUserId()).ifPresent(user -> {
                        dto.setFullName(user.getFullName());
                        dto.setProfilePic(user.getProfilePicUrl());
                        dto.setPhoneNumber(user.getPhoneNumber());
                    });

                    return dto;
                })
                .collect(Collectors.toList());

        // Log the results
        log.info("Total matching trips found for user {}: {}", userId, response.size());

        return response;
    }
}