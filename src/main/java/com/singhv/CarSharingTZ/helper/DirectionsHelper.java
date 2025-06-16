package com.singhv.CarSharingTZ.helper;

import com.singhv.CarSharingTZ.models.Points;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DirectionsHelper {

    @Autowired
    private GeoApiContext geoApiContext;

    @Value("${minDistanceInMetersBetween2PickupPoints}")
    private int minDistanceInMeters;

    /**
     * Determines if two pickup points are within the minimum acceptable distance from each other.
     *
     * @param pickupPointA First pickup point
     * @param pickupPointB Second pickup point
     * @return true if the points are close enough, false otherwise
     */
    public boolean isPickupNearBy(Points pickupPointA, Points pickupPointB) {
        LatLng pointA = new LatLng(pickupPointA.getLatitude(), pickupPointA.getLongitude());
        LatLng pointB = new LatLng(pickupPointB.getLatitude(), pickupPointB.getLongitude());
        DirectionsResult result = null;

        try {
            result = DirectionsApi.getDirections(geoApiContext, pointA.toString(), pointB.toString())
                    .alternatives(false)
                    .mode(TravelMode.WALKING)
                    .units(Unit.METRIC)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("Error getting directions between points", e);
            return false;
        }

        if (result != null && result.routes.length > 0) {
            long distanceInMeters = result.routes[0].legs[0].distance.inMeters;
            log.debug("Distance between points: {} meters", distanceInMeters);
            return distanceInMeters <= minDistanceInMeters;
        }
        return false;
    }

    /**
     * Calculates the distance from a point to a polyline.
     *
     * @param pickupPointA Starting point of the polyline
     * @param destinationPointB Ending point of the polyline
     * @param requestedPoint The point to calculate distance from
     * @return The distance in meters
     */
    public double getDistanceFromAPointFromPolyline(Points pickupPointA, Points destinationPointB, Points requestedPoint) {
        LatLng pickupLatLng = new LatLng(pickupPointA.getLatitude(), pickupPointA.getLongitude());
        LatLng destinationLatLng = new LatLng(destinationPointB.getLatitude(), destinationPointB.getLongitude());
        LatLng requestedLatLng = new LatLng(requestedPoint.getLatitude(), requestedPoint.getLongitude());

        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .origin(pickupLatLng)
                    .destination(destinationLatLng)
                    .mode(TravelMode.DRIVING)
                    .waypoints(requestedLatLng)
                    .await();

            // Calculate distance from requested point to the route
            if (result.routes.length > 0 && result.routes[0].legs.length > 0) {
                return result.routes[0].legs[0].distance.inMeters;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("Error calculating distance from requested point to polyline", e);
        }

        return Double.MAX_VALUE; // Return a high value if the distance cannot be calculated
    }
}