package com.singhv.CarSharingTZ.config;

import com.google.maps.GeoApiContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GoogleMapsConfig {

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;
    @Bean
    public GeoApiContext geoApiContext() {
        log.info("Initializing Google Maps API with key: {}", apiKey.substring(0, 5) + "...");
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }
}