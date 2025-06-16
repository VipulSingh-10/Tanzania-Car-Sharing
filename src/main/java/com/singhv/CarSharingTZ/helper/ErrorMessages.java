package com.singhv.CarSharingTZ.helper;

/**
 * Standard error messages used throughout the application.
 */
public interface ErrorMessages {
    String EMPTY = "";
    String USER_NOT_EXISTS = "User does not exist";
    String SOME_UNEXPECTED_ERROR_OCCUR = "An unexpected error occurred";
    String NO_RIDES_JOINED = "There are no ride requests or rides for your trip";
    String NO_TRIPS_FOUND = "There are no current commuters for your search";
    String NO_DATA_AVAILABE = "No data available";
    String WRONG_PASSWORD = "Wrong password";
    String RIDER_ALREADY_JOINED = "Rider has already joined the ride. Cannot join again";
    String NO_SEATS_AVAILABLE_IN_TRIP = "There are no seats available in this trip";
    String TRIP_ALREADY_ENDED = "Sorry! Trip has already completed, cannot join";
    String SAME_VEHICLE_REGISTRATION_ATTEMPT = "Sorry! You may be trying to register a vehicle that is already registered";

    String RIDE_CANCELLATION_FAILED = "Failed to cancel the ride";
    String RIDE_NOT_FOUND = "Ride not found for the user";
}