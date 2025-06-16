package com.singhv.CarSharingTZ.service;

import com.singhv.CarSharingTZ.dto.VehicleRegisterRequestDTO;
import com.singhv.CarSharingTZ.dto.VehicleResponseDTO;
import com.singhv.CarSharingTZ.dto.VehicleTypeDTO;

import java.util.List;

public interface VehicleService {
    List<VehicleTypeDTO> getVehicleTypes();

    List<VehicleResponseDTO> getUserVehicles(String userId);

    boolean addNewVehicle(String userId, VehicleRegisterRequestDTO registerRequestDTO);
}
