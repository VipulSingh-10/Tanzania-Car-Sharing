package com.singhv.vehicleservice.service;

import com.singhv.vehicleservice.dto.VehicleRegisterRequestDTO;
import com.singhv.vehicleservice.dto.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {
    List<VehicleResponseDTO> getUserVehicles(String userId);
    boolean addNewVehicle(String userId, VehicleRegisterRequestDTO registerRequestDTO);
}