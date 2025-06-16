package com.singhv.CarSharingTZ.repository;

import com.singhv.CarSharingTZ.models.Riders;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRideRepository extends MongoRepository<Riders, String> {
}
