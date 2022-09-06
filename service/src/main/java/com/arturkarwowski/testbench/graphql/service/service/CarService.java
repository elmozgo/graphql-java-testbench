package com.arturkarwowski.testbench.graphql.service.service;

import com.arturkarwowski.testbench.graphql.service.client.dto.VehicleDto;
import com.arturkarwowski.testbench.graphql.api.Car;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    private final DescriptionSanitiser descriptionSanitiser;

    public CarService(DescriptionSanitiser descriptionSanitiser) {
        this.descriptionSanitiser = descriptionSanitiser;
    }

    public Car buildFrom(VehicleDto vehicle) {

        var sanitisedDescription = descriptionSanitiser.sanitise(vehicle.getDescription());

        return new Car(vehicle.getLicencePlate(), vehicle.getDriverId(), sanitisedDescription);
    }
}
