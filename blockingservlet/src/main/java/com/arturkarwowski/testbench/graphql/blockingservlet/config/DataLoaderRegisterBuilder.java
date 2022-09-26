package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.CarFacade;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.DrivingFineFacade;
import org.dataloader.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataLoaderRegisterBuilder {

    private final CarFacade carFacade;
    private final DrivingFineFacade drivingFineFacade;
    private final Executor tracingExecutor;

    public DataLoaderRegisterBuilder(CarFacade carFacade, DrivingFineFacade drivingFineFacade, Executor tracingExecutor) {
        this.carFacade = carFacade;
        this.drivingFineFacade = drivingFineFacade;
        this.tracingExecutor = tracingExecutor;
    }

    private DataLoader<String, Car> carsDataLoader() {

        MappedBatchLoader<String, Car> carsBatchLoader = licencePlates -> {
            return CompletableFuture.supplyAsync(() -> {
                var cars = carFacade.getCars(licencePlates.stream().toList());
                return cars.stream().collect(Collectors.toMap(Car::getLicencePlate, Function.identity()));
            }, tracingExecutor);
        };

        return DataLoaderFactory.newMappedDataLoader(carsBatchLoader);
    }

    private DataLoader<String, List<DrivingFine>> drivingFinesDataLoader() {

        BatchLoader<String, List<DrivingFine>> drivingFinesBatchLoader = driverIds ->
                CompletableFuture.supplyAsync(() ->
                        driverIds.stream().map(drivingFineFacade::getDrivingFines).collect(Collectors.toList()), tracingExecutor);

        return DataLoaderFactory.newDataLoader(drivingFinesBatchLoader);
    }

    public DataLoaderRegistry buildDataLoaderRegistry() {
        return new DataLoaderRegistry()
                .register("cars", carsDataLoader())
                .register("drivingFines", drivingFinesDataLoader());
    }
}
