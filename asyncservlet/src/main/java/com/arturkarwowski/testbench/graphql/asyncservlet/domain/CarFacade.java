package com.arturkarwowski.testbench.graphql.asyncservlet.domain;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.asyncservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import com.arturkarwowski.testbench.graphql.service.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CarFacade {

    private static final Logger logger = LoggerFactory.getLogger(CarFacade.class);

    private final FleetManagerClient fleetManagerClient;
    private final CarService carService;

    public CarFacade(FleetManagerClient fleetManagerClient, CarService carService) {
        this.fleetManagerClient = fleetManagerClient;
        this.carService = carService;
    }

    public CompletableFuture<List<Car>> getCars(List<String> licencePlates) {

        var vehicles = fleetManagerClient.getVehicles(licencePlates).thenApply(VehiclesResponse::getVehicles);

        vehicles.thenRun(() -> logger.debug("called fleet manager api with {} licence plates", licencePlates.size()));

        return vehicles.thenApply((v) ->
            v.stream().map(carService::buildFrom).collect(Collectors.toList()));
    }

}
