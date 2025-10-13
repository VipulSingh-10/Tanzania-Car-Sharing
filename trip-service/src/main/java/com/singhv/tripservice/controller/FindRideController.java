package com.singhv.tripservice.controller;

import com.singhv.common.dto.RequestDTO;
import com.singhv.common.dto.ResponseDTO;
import com.singhv.tripservice.dto.FindRideRequestDTO;
import com.singhv.tripservice.model.Trips;
import com.singhv.tripservice.service.TripGeospatialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for finding available rides based on user's travel requirements
 */
@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Slf4j
public class FindRideController {

    private final TripGeospatialService geoService;

    /**
     * Find available rides matching the user's source and destination
     *
     * @param request Contains pickup and dropoff location details with search radius
     * @return List of matching trips
     */
    @PostMapping("/find-ride")
    public ResponseEntity<ResponseDTO<List<Trips>>> findRide(
            @RequestBody RequestDTO<FindRideRequestDTO> request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        FindRideRequestDTO rideRequest = request.getRequestContent();

        // Use userId from header if available, otherwise from request body
        String effectiveUserId = userId != null ? userId : request.getUserId();

        log.info("Finding rides for user: {} from ({}, {}) to ({}, {})",
                effectiveUserId,
                rideRequest.getPickupLatitude(),
                rideRequest.getPickupLongitude(),
                rideRequest.getDropoffLatitude(),
                rideRequest.getDropoffLongitude());

        // Default radius to 5km if not provided
        double sourceRadius = rideRequest.getPickupRadiusKm() != null ? rideRequest.getPickupRadiusKm() : 5.0;
        double destRadius = rideRequest.getDropoffRadiusKm() != null ? rideRequest.getDropoffRadiusKm() : 5.0;

        List<Trips> matchingTrips = geoService.findTripsMatchingRoute(
                rideRequest.getPickupLatitude(),
                rideRequest.getPickupLongitude(),
                sourceRadius,
                rideRequest.getDropoffLatitude(),
                rideRequest.getDropoffLongitude(),
                destRadius
        );

        log.info("Found {} matching trips for user: {}", matchingTrips.size(), effectiveUserId);

        ResponseDTO<List<Trips>> response = new ResponseDTO<>();
        response.setSuccess(true);
        response.setResponseContent(matchingTrips);

        return ResponseEntity.ok(response);
    }
}
