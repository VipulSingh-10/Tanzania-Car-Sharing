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

        Point sourceLocation = new Point(sourceLon, sourceLat);
        Distance sourceDistance = new Distance(sourceRadiusKm, Metrics.KILOMETERS);

        Point destLocation = new Point(destLon, destLat);
        Distance destDistance = new Distance(destRadiusKm, Metrics.KILOMETERS);

        Query query = new Query();
        query.addCriteria(Criteria.where("sourceLocation")
                .nearSphere(sourceLocation)
                .maxDistance(sourceDistance.getNormalizedValue()));
        query.addCriteria(Criteria.where("destinationLocation")
                .nearSphere(destLocation)
                .maxDistance(destDistance.getNormalizedValue()));
        query.addCriteria(Criteria.where("tripStatus").is("OFFERED"));

        List<Trips> trips = mongoTemplate.find(query, Trips.class);
        log.info("Found {} trips matching route from ({}, {}) to ({}, {}) within {} km and {} km",
                trips.size(), sourceLat, sourceLon, destLat, destLon, sourceRadiusKm, destRadiusKm);

        return trips;
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
}

