package com.singhv.CarSharingTZ.repository;

import com.singhv.CarSharingTZ.models.Trips;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository for accessing and managing trip information.
 */
@Repository
public interface TripsRepository extends MongoRepository<Trips, String> {

    /**
     * Find all trips created by a specific user
     */
    List<Trips> findAllByUserId(@Param("riderUserId") String riderUserId);

    /**
     * Find all trips with a specific status created by a user
     */
    List<Trips> findAllByUserIdAndTripStatus(
            @Param("riderUserId") String riderUserId,
            @Param("tripStatus") String tripStatus);

    /**
     * Find trips scheduled within a time range
     */
    List<Trips> findByTripStartTimeBetween(
            @Param("startTime") Date dateTimeBeforeNow,
            @Param("endTime") Date dateTimeAfterNow);

    /**
     * Find a trip by user and start time
     */
    Trips findByUserIdAndTripStartTime(
            @Param("userId") String userId,
            @Param("tripStartTime") Date tripStartTime);

    /**
     * Find a trip by its ID
     */
    Trips findByTripId(@Param("tripId") String tripId);

    List<Trips> findByTripStatus(@NotBlank String tripStatus);

}