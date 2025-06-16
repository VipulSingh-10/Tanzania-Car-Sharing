package com.singhv.CarSharingTZ.controller;

import com.singhv.CarSharingTZ.dto.*;
import com.singhv.CarSharingTZ.helper.ErrorMessages;
import com.singhv.CarSharingTZ.service.MyRidesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/myrides")
public class MyRidesController {

    @Autowired
    private MyRidesService myRidesService;

    @PostMapping(value = "/upcoming", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseListDTO<RideBasicInfoDTO> getUpcomingRides(
            @RequestBody RequestDTO<Void> upcomingRideReq) {
        log.info("UpcomingRideReq--> " + upcomingRideReq);
        ResponseListDTO<RideBasicInfoDTO> response = new ResponseListDTO<>();

        try {
            List<RideBasicInfoDTO> result = myRidesService
                    .getUpcomingRides(upcomingRideReq.getUserId());
            if (!result.isEmpty()) {
                response.setResponseContent(result);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(null);
                response.setSuccess(true);
                response.setErrorMessage(ErrorMessages.NO_DATA_AVAILABE); // Note: There's a typo in the constant name that should be fixed in ErrorMessages class
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }

    @PostMapping(value = "/history", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseListDTO<RideBasicInfoDTO> getHistoryRides(
            @RequestBody RequestDTO<Void> historyRideReq) {
        log.info("HistoryRideReq--> " + historyRideReq);
        ResponseListDTO<RideBasicInfoDTO> response = new ResponseListDTO<>();

        try {
            List<RideBasicInfoDTO> result = myRidesService
                    .getHistoryRides(historyRideReq.getUserId());
            if (!result.isEmpty()) {
                response.setResponseContent(result);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(null);
                response.setSuccess(true);
                response.setErrorMessage(ErrorMessages.NO_DATA_AVAILABE); // Note: Same typo here
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }
    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CancelRideResponseDTO> cancelRide(
            @RequestBody RequestDTO<CancelRideRequestDTO> cancelRideReq) {
        log.info("CancelRideReq--> " + cancelRideReq);
        ResponseDTO<CancelRideResponseDTO> response = new ResponseDTO<>();

        try {
            CancelRideResponseDTO result = myRidesService
                    .cancelRide(cancelRideReq.getUserId(), cancelRideReq.getRequestContent());

            if (result.isRideCancelled()) {
                response.setResponseContent(result);
                response.setSuccess(true);
                response.setErrorMessage(null);
            } else {
                response.setResponseContent(result);
                response.setSuccess(false);
                response.setErrorMessage(result.getErrMsg());
            }
        } catch (Exception exp) {
            response.setResponseContent(null);
            response.setSuccess(false);
            response.setErrorMessage(exp.getMessage());
            exp.printStackTrace();
        }
        return response;
    }
}