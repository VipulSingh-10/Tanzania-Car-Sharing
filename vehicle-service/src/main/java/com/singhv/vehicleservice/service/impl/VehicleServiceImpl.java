package com.singhv.vehicleservice.service.impl;

import com.singhv.vehicleservice.dto.VehicleRegisterRequestDTO;
import com.singhv.vehicleservice.dto.VehicleResponseDTO;
import com.singhv.vehicleservice.model.Vehicle;
import com.singhv.vehicleservice.repository.VehicleRepository;
import com.singhv.vehicleservice.service.VehicleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<VehicleResponseDTO> getUserVehicles(String userId) {
        List<VehicleResponseDTO> responseDTO = new ArrayList<>();
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(userId);
        for (Vehicle vehicle : userVehicles) {
            VehicleResponseDTO vehicleResponseDTO = new VehicleResponseDTO();
            vehicleResponseDTO.setValue(vehicle.getVehicleNumber());
            vehicleResponseDTO.setText(vehicle.getVehicleName() + " | " + vehicle.getVehicleColor());
            vehicleResponseDTO.setSeatingCapacity(vehicle.getSeatingCapacity());
            responseDTO.add(vehicleResponseDTO);
        }
        return responseDTO;
    }

    @Override
    public boolean addNewVehicle(String userId, VehicleRegisterRequestDTO registerRequestDTO) {
        if (vehicleRepository.existsByVehicleNumber(registerRequestDTO.getVehicleNumber().toUpperCase())) {
            return false;
        }
        Vehicle newVehicle = new Vehicle();
        BeanUtils.copyProperties(registerRequestDTO, newVehicle);
        newVehicle.setUserId(userId);
        vehicleRepository.save(newVehicle);
        return true;
    }
}