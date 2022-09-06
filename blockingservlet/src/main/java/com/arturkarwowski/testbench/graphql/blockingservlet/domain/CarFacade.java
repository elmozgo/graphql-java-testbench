package com.arturkarwowski.testbench.graphql.blockingservlet.domain;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.blockingservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CarFacade {

    private static final Logger logger = LoggerFactory.getLogger(CarFacade.class);

    private final FleetManagerClient fleetManagerClient;
    private final CarService carService;

    public CarFacade(FleetManagerClient fleetManagerClient, CarService carService) {
        this.fleetManagerClient = fleetManagerClient;
        this.carService = carService;
    }

    public List<Car> getCars(List<String> licencePlates) {

        var vehicles = fleetManagerClient.getVehicles(licencePlates).getVehicles();
        logger.debug("called fleet manager api with {} licence plates", licencePlates.size());

        return vehicles.stream().map(carService::buildFrom).collect(Collectors.toList());
    }

    public Optional<Car> getCar(String licencePlate) {

        return getCars(List.of(licencePlate)).stream().findFirst();
    }

}
