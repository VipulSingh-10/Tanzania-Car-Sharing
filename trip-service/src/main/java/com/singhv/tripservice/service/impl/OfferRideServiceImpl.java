package com.singhv.tripservice.service.impl;
import com.singhv.common.dto.RequestDTO;
import com.singhv.common.dto.ResponseDTO;
import com.singhv.tripservice.dto.OfferRideRequestDTO;
import com.singhv.tripservice.dto.OfferRideResponseDTO;
import com.singhv.tripservice.model.Trips;
import com.singhv.tripservice.repository.TripsRepository;
import com.singhv.tripservice.service.OfferRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferRideServiceImpl implements OfferRideService {

    private final TripsRepository tripsRepository;

    @Override
    public ResponseDTO<OfferRideResponseDTO> offerRide(RequestDTO<OfferRideRequestDTO> offerRideRequest) {

        OfferRideRequestDTO requestContent = offerRideRequest.getRequestContent();
        OfferRideResponseDTO responseContent = new OfferRideResponseDTO();
        ResponseDTO<OfferRideResponseDTO> responseDTO = new ResponseDTO<>();

        // Convert the ZonedDateTime to UTC Instant for storage
        Instant tripStartInstant = requestContent.getTripStartDateTime().toInstant();
        String tripTimezone = requestContent.getTripStartDateTime().getZone().getId();

        // Check if the driver already has a trip at overlapping time
        // We check for trips within +/- 12 hours to handle timezone overlaps
        Instant rangeStart = tripStartInstant.minus(12, ChronoUnit.HOURS);
        Instant rangeEnd = tripStartInstant.plus(12, ChronoUnit.HOURS);

        List<Trips> existingTrips = tripsRepository.findByDriverIdAndTripStartDateTimeUTCBetween(
                offerRideRequest.getUserId(),
                rangeStart,
                rangeEnd
        );

        // More precise check: trips within 1 hour window
        boolean hasConflict = existingTrips.stream()
                .anyMatch(trip -> {
                    long hoursDiff = Math.abs(ChronoUnit.HOURS.between(trip.getTripStartDateTimeUTC(), tripStartInstant));
                    return hoursDiff < 1;
                });

        if (hasConflict) {
            responseContent.setErrorMessage("Driver already has a trip at the same time. Please choose a different time.");
            responseContent.setTripCreated(false);
            responseDTO.setResponseContent(responseContent);
            return responseDTO;
        }

        // Create a new trip - store in UTC for universal consistency
        Trips newTrip = Trips.builder()
                .driverId(offerRideRequest.getUserId())
                .tripStatus("OFFERED")
                .vehicleNumber(requestContent.getVehicleNumber())
                .sourceAddress(requestContent.getSourceAddress())
                .destinationAddress(requestContent.getDestinationAddress())
                .offeredSeat(requestContent.getOfferedSeat())
                .currSeats(0)
                .tripStartDateTimeUTC(tripStartInstant)  // Store in UTC
                .tripTimezone(tripTimezone)  // Store original timezone for reference
                .pricePerKm(10.0)
                .build();

        Trips savedTrip = tripsRepository.save(newTrip);

        // Build response with timezone information
        responseContent.setTripId(savedTrip.getTripId());
        responseContent.setVehicleNumber(savedTrip.getVehicleNumber());
        responseContent.setSourceAddress(savedTrip.getSourceAddress());
        responseContent.setDestinationAddress(savedTrip.getDestinationAddress());
        responseContent.setTripStartDateTime(requestContent.getTripStartDateTime());
        responseContent.setTripTimezone(tripTimezone);
        responseContent.setTripCreated(true);

        responseDTO.setResponseContent(responseContent);
        return responseDTO;
    }

}
