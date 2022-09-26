package com.arturkarwowski.testbench.graphql.asyncservlet.config;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.asyncservlet.domain.DriverFacade;
import com.arturkarwowski.testbench.graphql.asyncservlet.domain.DrivingFineFacade;
import graphql.schema.DataFetcher;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.function.UnaryOperator.identity;

@Component
public class DataFetcherDefinitions {

    private DriverFacade driverFacade;
    private DrivingFineFacade drivingFineFacade;

    public DataFetcherDefinitions(DriverFacade driverFacade, DrivingFineFacade drivingFineFacade) {
        this.driverFacade = driverFacade;
        this.drivingFineFacade = drivingFineFacade;
    }
    final DataFetcher<CompletableFuture<Optional<Car>>> carFetcher = (environment) -> {

            String licencePlate = environment.getArgument("licencePlate");
            DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

            return dataLoader.load(licencePlate).thenApply(Optional::ofNullable);
    };

    final DataFetcher<CompletableFuture<Driver>> driverFetcher = (environment) -> {

        Car car = environment.getSource();
        return driverFacade.getDriver(car.getDriverId());
    };

    final DataFetcher<CompletableFuture<List<DrivingFine>>> penaltiesFetcher = (environment) -> {

        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");
        return  dataLoader.load(driver.getId());
    };

    final DataFetcher<CompletableFuture<Integer>> activePenaltyPointsFetcher = (environment) -> {
        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");

        var futureFines = dataLoader.load(driver.getId());

        return futureFines.thenApply(drivingFineFacade::getActivePenaltyPoints);
    };

    final DataFetcher<CompletableFuture<Optional<Car>>> drivingFineCarFetcher = (environment) -> {
        DrivingFine drivingFine = environment.getSource();
        DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

        var futureCar = dataLoader.load(drivingFine.getCarLicencePlate());
        return futureCar.thenApply(Optional::ofNullable);
    };

}
