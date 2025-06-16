package com.singhv.CarSharingTZ.repository;

import com.singhv.CarSharingTZ.models.Vehicles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing and managing vehicle information.
 */
@Repository
public interface VehicleRepository extends MongoRepository<Vehicles, String> {

    /**
     * Find all vehicles owned by a specific user
     */
    List<Vehicles> findByUserId(@Param("userId") String userId);

    /**
     * Check if a vehicle with the given registration number already exists
     * Note: The method name suggests the input might be transformed to uppercase before checking
     */
    boolean existsByVehicleNumber(@Param("vehicleNumber") String vehicleNumber);
}