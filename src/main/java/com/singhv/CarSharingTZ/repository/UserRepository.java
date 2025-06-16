package com.singhv.CarSharingTZ.repository;

import com.singhv.CarSharingTZ.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing and managing user information.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their unique user ID
     */
    User findByUserId(@Param("userId") String userId);

    /**
     * Find a user by their email address
     */
    User findByEmailId(@Param("emailId") String emailId);
}