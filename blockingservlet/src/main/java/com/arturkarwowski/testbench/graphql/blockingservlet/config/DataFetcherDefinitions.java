package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.DriverFacade;
import com.arturkarwowski.testbench.graphql.blockingservlet.domain.DrivingFineFacade;
import graphql.schema.DataFetcher;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataFetcherDefinitions {

    private DriverFacade driverFacade;
    private DrivingFineFacade drivingFineFacade;

    public DataFetcherDefinitions(DriverFacade driverFacade, DrivingFineFacade drivingFineFacade) {
        this.driverFacade = driverFacade;
        this.drivingFineFacade = drivingFineFacade;
    }
    final DataFetcher<Optional<Car>> carFetcher = (environment) -> {

        String licencePlate = environment.getArgument("licencePlate");
        DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

        var futureCar = dataLoader.load(licencePlate);
        dataLoader.dispatch();
        return Optional.ofNullable(futureCar.join());
    };

    final DataFetcher<Driver> driverFetcher = (environment) -> {

        Car car = environment.getSource();
        return driverFacade.getDriver(car.getDriverId());
    };

    final DataFetcher<List<DrivingFine>> penaltiesFetcher = (environment) -> {

        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");

        var futureFines = dataLoader.load(driver.getId());
        dataLoader.dispatch();
        return futureFines.join();
    };

    final DataFetcher<Integer> activePenaltyPointsFetcher = (environment) -> {
        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");

        var futureFines = dataLoader.load(driver.getId());
        dataLoader.dispatch();
        var drivingFines =  futureFines.join();
        return drivingFineFacade.getActivePenaltyPoints(drivingFines);
    };

    final DataFetcher<Optional<Car>> drivingFineCarFetcher = (environment) -> {
        DrivingFine drivingFine = environment.getSource();
        DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

        var futureCar = dataLoader.load(drivingFine.getCarLicencePlate());
        dataLoader.dispatch();
        return Optional.ofNullable(futureCar.join());
    };

}
