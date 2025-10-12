package com.singhv.tripservice.model;

import com.singhv.common.models.Points;
import com.singhv.tripservice.dto.OSRM.Geometry;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "trips")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Trips {

    @Id
    @NotBlank
    private String tripId;
    private @NotBlank String tripStatus;
    @NotBlank
    private String vehicleNumber;
    @NotBlank
    private String driverId;
    @NotBlank
    private Points sourceAddress;
    @NotBlank
    private Points destinationAddress;
    @Min(1L)
    private int offeredSeat;
    private @Min(0L) int currSeats;

    // Store in UTC for consistency across timezones
    private Instant tripStartDateTimeUTC;

    // Store the timezone of the trip's source location (e.g., "Asia/Kolkata", "Europe/Berlin")
    private String tripTimezone;

    // Route information from OSRM
    private Geometry routeGeometry; // Full route path as GeoJSON LineString
    private double routeDistance; // Distance in meters
    private double routeDuration; // Duration in seconds

    private @PositiveOrZero double pricePerKm;
    private List<ObjectId> joinedRidersId;
    private Instant createdDate = Instant.now();
}
