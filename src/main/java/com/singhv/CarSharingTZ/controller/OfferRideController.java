package com.singhv.CarSharingTZ.controller;

import com.singhv.CarSharingTZ.dto.CreateTripResponseDTO;
import com.singhv.CarSharingTZ.dto.OfferRideDTO;
import com.singhv.CarSharingTZ.dto.RequestDTO;
import com.singhv.CarSharingTZ.dto.ResponseDTO;
import com.singhv.CarSharingTZ.service.OfferRideService;
import com.google.maps.errors.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/ride")
public class OfferRideController {

    @Autowired
    private OfferRideService offerRideService;

    /**
     * Create Trip
     *
     * @param tripDetails
     * @return
     */
    @PostMapping(value = "/create-trip", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CreateTripResponseDTO> createTrip(@RequestBody RequestDTO<OfferRideDTO> tripDetails) throws InterruptedException, ApiException, IOException {
        log.info("CreateTripRequest Params-->" + tripDetails.toString());
        ResponseDTO<CreateTripResponseDTO> responseDTO = new ResponseDTO<>();

        try {
            CreateTripResponseDTO createTripDTO = offerRideService.createTrip(tripDetails.getUserId(), tripDetails.getRequestContent());

            if (createTripDTO.isTripCreated()) {
                responseDTO.setSuccess(true);
                responseDTO.setErrorMessage(null);
                responseDTO.setResponseContent(createTripDTO);
            } else {
                responseDTO.setSuccess(false);
                responseDTO.setErrorMessage(createTripDTO.getErrMsg());
                responseDTO.setResponseContent(null);
            }
        } catch (Exception exp) {
            responseDTO.setSuccess(false);
            responseDTO.setErrorMessage(exp.getMessage());
            responseDTO.setResponseContent(null);
            exp.printStackTrace();
        }
        return responseDTO;
    }
}