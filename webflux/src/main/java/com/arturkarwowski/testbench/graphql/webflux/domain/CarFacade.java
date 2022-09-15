package com.arturkarwowski.testbench.graphql.webflux.domain;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import com.arturkarwowski.testbench.graphql.service.service.CarService;
import com.arturkarwowski.testbench.graphql.webflux.client.FleetManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
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

    public Mono<List<Car>> getCars(List<String> licencePlates) {

        var vehicles = fleetManagerClient.getVehicles(licencePlates).map(VehiclesResponse::getVehicles);

        return vehicles.doOnNext(v -> logger.debug("got fleet manager api response with {} licence plates", v.size()))
                .flatMapIterable(Function.identity())
                .map(carService::buildFrom)
                .collect(Collectors.toList());
    }

}
