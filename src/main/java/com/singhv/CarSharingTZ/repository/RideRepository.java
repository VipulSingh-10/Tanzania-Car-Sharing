package com.singhv.CarSharingTZ.repository;

import com.singhv.CarSharingTZ.models.Riders;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository for accessing and managing ride information.
 */
@Repository
public interface RideRepository extends MongoRepository<Riders, String> {

    /**
     * Find all rides for a specific user
     */
    List<Riders> findAllByUserId(@Param("riderUserId") String riderUserId);

    /**
     * Find all rides for a user with a specific status
     */
    List<Riders> findAllByUserIdAndRideStatus(
            @Param("riderUserId") String riderUserId,
            @Param("rideStatus") String rideStatus);

    /**
     * Find rides scheduled for today
     * Note: This method needs parameters to work correctly
     */
    // This method is likely incorrect - needs parameters
    // List<Riders> findByRideStartTime();

    /**
     * Find rides scheduled between two times
     */
    List<Riders> findByRideStartTimeBetween(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * Find a specific ride by user and trip
     */
    Riders findByUserIdAndAllottedTripId(
            @Param("userId") String userId,
            @Param("tripId") String tripId);

    /**
     * Find rides for a user with a specific status
     */
    List<Riders> findByUserIdAndRideStatus(
            @Param("riderUserId") String riderUserId,
            @Param("rideStatus") String rideStatus);

    /**
     * Find all rides for a user with any of the specified statuses
     */
    List<Riders> findAllByUserIdAndRideStatusIn(
            @Param("riderUserId") String riderUserId,
            @Param("rideStatus") List<String> rideStatus);

    List<Riders> findByAllottedTripId(String tripId);

}