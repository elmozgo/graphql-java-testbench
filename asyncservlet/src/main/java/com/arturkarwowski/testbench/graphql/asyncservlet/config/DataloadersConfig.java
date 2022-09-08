package com.arturkarwowski.testbench.graphql.asyncservlet.config;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.asyncservlet.domain.CarFacade;
import com.arturkarwowski.testbench.graphql.asyncservlet.domain.DrivingFineFacade;
import org.dataloader.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataloadersConfig {

    private final CarFacade carFacade;
    private final DrivingFineFacade drivingFineFacade;

    public DataloadersConfig(CarFacade carFacade, DrivingFineFacade drivingFineFacade) {
        this.carFacade = carFacade;
        this.drivingFineFacade = drivingFineFacade;
    }

    @Bean
    public DataLoader<String, Car> carsDataLoader() {

        MappedBatchLoader<String, Car> carsBatchLoader = licencePlates ->
                carFacade.getCars(licencePlates.stream().toList()).thenApply((cars) ->
                        cars.stream().collect(Collectors.toMap(Car::getLicencePlate, Function.identity())));

        return DataLoaderFactory.newMappedDataLoader(carsBatchLoader);
    }

    @Bean
    public DataLoader<String, List<DrivingFine>> drivingFinesDataLoader() {

        BatchLoader<String, List<DrivingFine>> drivingFinesBatchLoader = driverIds -> {
            var futures = driverIds.stream().map(drivingFineFacade::getDrivingFines).toList();
            var joined = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
            return joined.thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join)
                            .toList());
        };

        return DataLoaderFactory.newDataLoader(drivingFinesBatchLoader);
    }

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        return new DataLoaderRegistry()
                .register("cars", carsDataLoader())
                .register("drivingFines", drivingFinesDataLoader());
    }
}
