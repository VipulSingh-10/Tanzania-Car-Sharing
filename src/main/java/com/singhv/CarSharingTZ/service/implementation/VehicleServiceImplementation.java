package com.singhv.CarSharingTZ.service.implementation;

import com.singhv.CarSharingTZ.dto.VehicleRegisterRequestDTO;
import com.singhv.CarSharingTZ.dto.VehicleResponseDTO;
import com.singhv.CarSharingTZ.dto.VehicleTypeDTO;
import com.singhv.CarSharingTZ.models.Vehicles;
import com.singhv.CarSharingTZ.repository.VehicleRepository;
import com.singhv.CarSharingTZ.service.VehicleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleServiceImplementation implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private List<VehicleTypeDTO> resList = new ArrayList<VehicleTypeDTO>() {
        {
            add(new VehicleTypeDTO("HATCHBACK"));
            add(new VehicleTypeDTO("SUV"));
            add(new VehicleTypeDTO("SEDAN"));
        }
    };

    @Override
    public List<VehicleTypeDTO> getVehicleTypes() {
        return resList;
    }

    @Override
    public List<VehicleResponseDTO> getUserVehicles(String userId) {
        List<VehicleResponseDTO> responseDTO = new ArrayList<>();
        List<Vehicles> userVehicles = vehicleRepository.findByUserId(userId);
        for (Vehicles vehicle : userVehicles) {
            VehicleResponseDTO vehicleResponseDTO = new VehicleResponseDTO();
            vehicleResponseDTO.setValue(vehicle.getVehicleNumber());
            vehicleResponseDTO.setText(vehicle.getVehicleName() + " | " + vehicle.getVehicleColor());
            responseDTO.add(vehicleResponseDTO);
        }
        return responseDTO;
    }

    @Override
    public boolean addNewVehicle(String userId, VehicleRegisterRequestDTO registerRequestDTO) {
        if (vehicleRepository.existsByVehicleNumber(registerRequestDTO.getVehicleNumber().toUpperCase())) {
            return false;
        }
        Vehicles newVehicle = new Vehicles();
        BeanUtils.copyProperties(registerRequestDTO, newVehicle);
        newVehicle.setUserId(userId);
        vehicleRepository.save(newVehicle);
        return true;
    }
}
