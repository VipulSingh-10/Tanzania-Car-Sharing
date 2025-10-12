package com.singhv.tripservice.controller;

import com.singhv.common.dto.RequestDTO;
import com.singhv.common.dto.ResponseDTO;
import com.singhv.tripservice.dto.OfferRideRequestDTO;
import com.singhv.tripservice.dto.OfferRideResponseDTO;
import com.singhv.tripservice.service.OfferRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class OfferRideController {

    private final OfferRideService offerRideService;

    @PostMapping("/offer")
    public ResponseEntity<ResponseDTO<OfferRideResponseDTO>> offerRide(
            @RequestBody RequestDTO<OfferRideRequestDTO> request) {

        ResponseDTO<OfferRideResponseDTO> response = offerRideService.offerRide(request);

        if (response.getResponseContent().getTripCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
