package com.singhv.tripservice.service;

import com.singhv.tripservice.model.Trips;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for geospatial queries on trips
 * Allows searching for trips near a location
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripGeospatialService {

    private final MongoTemplate mongoTemplate;

    /**
     * Find trips with source location near the given coordinates
     *
     * @param latitude      Latitude of search point
     * @param longitude     Longitude of search point
     * @param radiusInKm    Search radius in kilometers
     * @return List of trips with source near the search point
     */
    public List<Trips> findTripsWithSourceNear(double latitude, double longitude, double radiusInKm) {
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);

        Query query = new Query();
        query.addCriteria(Criteria.where("sourceLocation")
                .nearSphere(location)
                .maxDistance(distance.getNormalizedValue()));
        query.addCriteria(Criteria.where("tripStatus").is("OFFERED"));

        List<Trips> trips = mongoTemplate.find(query, Trips.class);
        log.info("Found {} trips with source near ({}, {}) within {} km",
                trips.size(), latitude, longitude, radiusInKm);

        return trips;
    }

    /**
     * Find trips with destination location near the given coordinates
     *
     * @param latitude      Latitude of search point
     * @param longitude     Longitude of search point
     * @param radiusInKm    Search radius in kilometers
     * @return List of trips with destination near the search point
     */
    public List<Trips> findTripsWithDestinationNear(double latitude, double longitude, double radiusInKm) {
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);

        Query query = new Query();
        query.addCriteria(Criteria.where("destinationLocation")
                .nearSphere(location)
                .maxDistance(distance.getNormalizedValue()));
        query.addCriteria(Criteria.where("tripStatus").is("OFFERED"));

        List<Trips> trips = mongoTemplate.find(query, Trips.class);
        log.info("Found {} trips with destination near ({}, {}) within {} km",
                trips.size(), latitude, longitude, radiusInKm);

        return trips;
    }

    /**
     * Find trips matching both source and destination within given radius
     * Note: MongoDB doesn't allow multiple $geoNear operations in one query,
     * so we search by source first, then filter by destination in memory
     *
     * @param sourceLat         Source latitude
     * @param sourceLon         Source longitude
     * @param sourceRadiusKm    Source search radius in km
     * @param destLat           Destination latitude
     * @param destLon           Destination longitude
     * @param destRadiusKm      Destination search radius in km
     * @return List of matching trips
     */
    public List<Trips> findTripsMatchingRoute(
            double sourceLat, double sourceLon, double sourceRadiusKm,
            double destLat, double destLon, double destRadiusKm) {

        // Step 1: Find trips with source near the pickup point
        Point sourceLocation = new Point(sourceLon, sourceLat);
        Distance sourceDistance = new Distance(sourceRadiusKm, Metrics.KILOMETERS);

        Query query = new Query();
        query.addCriteria(Criteria.where("sourceLocation")
                .nearSphere(sourceLocation)
                .maxDistance(sourceDistance.getNormalizedValue()));
        query.addCriteria(Criteria.where("tripStatus").is("OFFERED"));

        List<Trips> tripsNearSource = mongoTemplate.find(query, Trips.class);

        log.info("Found {} trips near source ({}, {}) within {} km",
                tripsNearSource.size(), sourceLat, sourceLon, sourceRadiusKm);

        // Step 2: Filter results by destination proximity
        Point destLocation = new Point(destLon, destLat);
        double destRadiusInMeters = destRadiusKm * 1000;

        List<Trips> matchingTrips = tripsNearSource.stream()
                .filter(trip -> {
                    if (trip.getDestinationLocation() == null) {
                        return false;
                    }
                    double distance = calculateDistance(
                            destLat, destLon,
                            trip.getDestinationLocation().getY(),
                            trip.getDestinationLocation().getX()
                    );
                    return distance <= destRadiusInMeters;
                })
                .toList();

        log.info("Found {} trips matching route from ({}, {}) to ({}, {}) within {} km and {} km",
                matchingTrips.size(), sourceLat, sourceLon, destLat, destLon, sourceRadiusKm, destRadiusKm);

        return matchingTrips;
    }

    /**
     * Find trips within a specific area (bounding box)
     *
     * @param minLat Minimum latitude
     * @param minLon Minimum longitude
     * @param maxLat Maximum latitude
     * @param maxLon Maximum longitude
     * @return List of trips with source within the bounding box
     */
    public List<Trips> findTripsInArea(double minLat, double minLon, double maxLat, double maxLon) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sourceLocation")
                .within(new org.springframework.data.mongodb.core.geo.GeoJsonPolygon(
                        new Point(minLon, minLat),
                        new Point(maxLon, minLat),
                        new Point(maxLon, maxLat),
                        new Point(minLon, maxLat),
                        new Point(minLon, minLat)
                )));
        query.addCriteria(Criteria.where("tripStatus").is("OFFERED"));

        List<Trips> trips = mongoTemplate.find(query, Trips.class);
        log.info("Found {} trips in area ({}, {}) to ({}, {})",
                trips.size(), minLat, minLon, maxLat, maxLon);

        return trips;
    }

    /**
     * Calculate distance between two points using Haversine formula
     * @return distance in meters
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // meters

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
