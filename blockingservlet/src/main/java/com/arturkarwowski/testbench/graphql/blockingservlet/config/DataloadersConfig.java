package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.CarFacade;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.DrivingFineFacade;
import org.dataloader.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Configuration
public class DataloadersConfig {

    private final CarFacade carFacade;
    private final DrivingFineFacade drivingFineFacade;
    private final Executor tracingExecutor;

    public DataloadersConfig(CarFacade carFacade, DrivingFineFacade drivingFineFacade, Executor tracingExecutor) {
        this.carFacade = carFacade;
        this.drivingFineFacade = drivingFineFacade;
        this.tracingExecutor = tracingExecutor;
    }

    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataLoader<String, Car> carsDataLoader() {

        MappedBatchLoader<String, Car> carsBatchLoader = licencePlates -> {
            return CompletableFuture.supplyAsync(() -> {
                var cars = carFacade.getCars(licencePlates.stream().toList());
                return cars.stream().collect(Collectors.toMap(Car::getLicencePlate, Function.identity()));
            }, tracingExecutor);
        };

        return DataLoaderFactory.newMappedDataLoader(carsBatchLoader);
    }

    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataLoader<String, List<DrivingFine>> drivingFinesDataLoader() {

        BatchLoader<String, List<DrivingFine>> drivingFinesBatchLoader = driverIds ->
                CompletableFuture.supplyAsync(() ->
                        driverIds.stream().map(drivingFineFacade::getDrivingFines).collect(Collectors.toList()), tracingExecutor);

        return DataLoaderFactory.newDataLoader(drivingFinesBatchLoader);
    }

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        return new DataLoaderRegistry()
                .register("cars", carsDataLoader())
                .register("drivingFines", drivingFinesDataLoader());
    }
}
