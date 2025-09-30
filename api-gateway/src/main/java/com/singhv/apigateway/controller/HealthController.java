package com.singhv.apigateway.controller;

import com.singhv.common.dto.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseDTO<String> healthCheck() {

        ResponseDTO<String> response = new ResponseDTO();
        response.setSuccess(true);
        response.setErrorMessage(null);
        response.setResponseContent("API Gateway is up and running!");
        return response;
    }
}
