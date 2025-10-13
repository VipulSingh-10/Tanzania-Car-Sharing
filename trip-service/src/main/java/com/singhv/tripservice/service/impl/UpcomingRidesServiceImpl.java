package com.singhv.tripservice.service.impl;

import com.singhv.tripservice.dto.DriverUpcomingTripDTO;
import com.singhv.tripservice.dto.PassengerUpcomingRideDTO;
import com.singhv.tripservice.model.Trips;
import com.singhv.tripservice.repository.TripsRepository;
import com.singhv.tripservice.service.TimezoneConversionService;
import com.singhv.tripservice.service.UpcomingRidesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpcomingRidesServiceImpl implements UpcomingRidesService {

    private final TripsRepository tripsRepository;
    private final TimezoneConversionService timezoneService;

    @Override
    public List<DriverUpcomingTripDTO> getDriverUpcomingTrips(String driverId) {
        log.info("Fetching upcoming trips for driver: {}", driverId);
        
        // Get all trips for this driver starting from now
        Instant now = Instant.now();
        List<Trips> upcomingTrips = tripsRepository.findByDriverIdAndTripStartDateTimeUTCBetween(
                driverId,
                now,
                now.plusSeconds(365L * 24 * 60 * 60) // Next year
        );
        
        log.info("Found {} upcoming trips for driver: {}", upcomingTrips.size(), driverId);
        
        return upcomingTrips.stream()
                .filter(trip -> "OFFERED".equals(trip.getTripStatus()) || "CONFIRMED".equals(trip.getTripStatus()))
                .map(this::convertToDriverUpcomingTripDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PassengerUpcomingRideDTO> getPassengerUpcomingRides(String passengerId) {
        log.info("Fetching upcoming rides for passenger: {}", passengerId);
        
        // TODO: This requires a Rider/Booking entity to track passenger bookings
        // For now, return empty list - will need to implement booking system
        // This should query a separate "bookings" or "riders" collection
        
        log.warn("Passenger booking system not yet implemented. Returning empty list.");
        return new ArrayList<>();
    }

    /**
     * Convert Trips entity to DriverUpcomingTripDTO
     */
    private DriverUpcomingTripDTO convertToDriverUpcomingTripDTO(Trips trip) {
        // Convert UTC time to original trip timezone
        ZonedDateTime tripTimeInOriginalZone = timezoneService.convertToTripTimezone(
                trip.getTripStartDateTimeUTC(),
                trip.getTripTimezone()
        );
        
        int bookedSeats = trip.getCurrSeats();
        int availableSeats = trip.getOfferedSeat() - bookedSeats;
        
        // Calculate estimated earnings
        double estimatedEarnings = (trip.getRouteDistance() / 1000.0) * trip.getPricePerKm();
        
        return DriverUpcomingTripDTO.builder()
                .tripId(trip.getTripId())
                .driverId(trip.getDriverId())
                .vehicleNumber(trip.getVehicleNumber())
                .tripStatus(trip.getTripStatus())
                .sourceAddress(trip.getSourceAddress())
                .destinationAddress(trip.getDestinationAddress())
                .tripStartDateTime(tripTimeInOriginalZone)
                .tripTimezone(trip.getTripTimezone())
                .offeredSeat(trip.getOfferedSeat())
                .availableSeats(availableSeats)
                .bookedSeats(bookedSeats)
                .passengers(new ArrayList<>()) // TODO: Fetch from bookings collection
                .routeDistanceInKm(trip.getRouteDistance() / 1000.0)
                .routeDurationInMinutes(trip.getRouteDuration() / 60.0)
                .pricePerKm(trip.getPricePerKm())
                .estimatedEarnings(estimatedEarnings)
                .build();
    }
}

