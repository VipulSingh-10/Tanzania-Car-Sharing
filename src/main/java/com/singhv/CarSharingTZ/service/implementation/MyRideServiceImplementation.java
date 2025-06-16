package com.singhv.CarSharingTZ.service.implementation;

import com.singhv.CarSharingTZ.dto.CancelRideRequestDTO;
import com.singhv.CarSharingTZ.dto.CancelRideResponseDTO;
import com.singhv.CarSharingTZ.dto.RideBasicInfoDTO;
import com.singhv.CarSharingTZ.models.Riders;
import com.singhv.CarSharingTZ.models.Trips;
import com.singhv.CarSharingTZ.models.enums.RideStatusEnum;
import com.singhv.CarSharingTZ.models.enums.TripStatusEnum;
import com.singhv.CarSharingTZ.repository.RideRepository;
import com.singhv.CarSharingTZ.repository.TripsRepository;
import com.singhv.CarSharingTZ.service.MyRidesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MyRideServiceImplementation implements MyRidesService {

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    public List<RideBasicInfoDTO> getUpcomingRides(String riderUserId) {

        List<RideBasicInfoDTO> result = new ArrayList<>();

        List<Riders> rides = rideRepository.findByUserIdAndRideStatus(riderUserId, RideStatusEnum.ALLOTTED_STATUS);
        log.info("UpcomingRide For " + riderUserId + " \n--> " + rides);

        for (Riders ride : rides) {
            RideBasicInfoDTO singleRide = new RideBasicInfoDTO();
            BeanUtils.copyProperties(ride, singleRide);
            singleRide.setTripId(ride.getAllottedTripId());
            singleRide.setTripStatus(ride.getRideStatus());
            Trips trip = tripsRepository.findByTripId(ride.getAllottedTripId());
            if (trip == null) continue;
            singleRide.setVehicleNumber(trip.getVehicleNumber());
            if (ride.isRiderTripOwner()) {
                singleRide.setSeats("Offered: " + trip.getOfferedSeats());
            } else {
                singleRide.setSeats("Requested: " + ride.getRequestedSeats());
            }
            result.add(singleRide);
        }

        return result.stream()
                .sorted(Comparator.comparing(RideBasicInfoDTO::getRideStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<RideBasicInfoDTO> getHistoryRides(String riderUserId) {

        List<RideBasicInfoDTO> result = new ArrayList<>();

        List<String> rideStatus = new ArrayList<String>() {
            {
                add(RideStatusEnum.COMPLETED_STATUS);
                add(RideStatusEnum.CANCELLED_STATUS);
            }
        };
        List<Riders> rides = rideRepository.findAllByUserIdAndRideStatusIn(riderUserId, rideStatus);
        log.info("HistoryRide For " + riderUserId + " \n--> " + rides);
        for (Riders ride : rides) {
            RideBasicInfoDTO singleRide = new RideBasicInfoDTO();
            BeanUtils.copyProperties(ride, singleRide);
            singleRide.setTripId(ride.getAllottedTripId());
            singleRide.setTripStatus(ride.getRideStatus());
            Trips trip = tripsRepository.findByTripId(ride.getAllottedTripId());
            if (trip == null) continue;
            singleRide.setVehicleNumber(trip.getVehicleNumber());
            if (ride.isRiderTripOwner()) {
                singleRide.setSeats("Offered: " + trip.getOfferedSeats());
            } else {
                singleRide.setSeats("Requested: " + ride.getRequestedSeats());
            }
            result.add(singleRide);
        }

        return result.stream()
                .sorted(Comparator.comparing(RideBasicInfoDTO::getRideStartTime))
                .collect(Collectors.toList());
    }
    @Override
    public CancelRideResponseDTO cancelRide(String userId, CancelRideRequestDTO requestContent) {
        CancelRideResponseDTO response = new CancelRideResponseDTO();

        try {
            // Find the ride to cancel
            Riders ride = rideRepository.findByUserIdAndAllottedTripId(userId, requestContent.getTripId());

            if (ride == null) {
                response.setRideCancelled(false);
                response.setErrMsg("Ride not found for the user");
                return response;
            }

            // Check if ride can be cancelled (e.g., not already completed or cancelled)
            if (RideStatusEnum.COMPLETED_STATUS.equals(ride.getRideStatus()) ||
                    RideStatusEnum.CANCELLED_STATUS.equals(ride.getRideStatus())) {
                response.setRideCancelled(false);
                response.setErrMsg("Cannot cancel a ride that is already " + ride.getRideStatus());
                return response;
            }

            // Update ride status to cancelled
            ride.setRideStatus(RideStatusEnum.CANCELLED_STATUS);
            rideRepository.save(ride);

            // If the user is the trip owner, handle differently
            if (ride.isRiderTripOwner()) {
                // Get the trip
                Trips trip = tripsRepository.findByTripId(ride.getAllottedTripId());
                if (trip != null) {
                    // Cancel the trip
                    trip.setTripStatus(TripStatusEnum.CANCELLED_STATUS);
                    tripsRepository.save(trip);

                    // Cancel all associated rides
                    List<Riders> allRidersForTrip = rideRepository.findByAllottedTripId(trip.getTripId());
                    for (Riders riderToCancel : allRidersForTrip) {
                        if (!userId.equals(riderToCancel.getUserId()) &&
                                !RideStatusEnum.CANCELLED_STATUS.equals(riderToCancel.getRideStatus()) &&
                                !RideStatusEnum.COMPLETED_STATUS.equals(riderToCancel.getRideStatus())) {
                            riderToCancel.setRideStatus(RideStatusEnum.CANCELLED_STATUS);
                            rideRepository.save(riderToCancel);
                            // Here you would also send notifications to affected riders
                        }
                    }
                }
            } else {
                // If the user is just a rider, update available seats in the trip
                // If the user is just a rider, update available seats in the trip
                Trips trip = tripsRepository.findByTripId(ride.getAllottedTripId());
                if (trip != null) {
                    // Decrease current seats when a rider cancels
                    trip.setCurrSeats(trip.getCurrSeats() - ride.getRequestedSeats());
                    // Make sure currSeats doesn't go below 0
                    if (trip.getCurrSeats() < 0) {
                        trip.setCurrSeats(0);
                    }
                    tripsRepository.save(trip);
                }
            }

            response.setRideCancelled(true);

        } catch (Exception e) {
            log.error("Error cancelling ride: ", e);
            response.setRideCancelled(false);
            response.setErrMsg("Error cancelling ride: " + e.getMessage());
        }

        return response;
    }
}
