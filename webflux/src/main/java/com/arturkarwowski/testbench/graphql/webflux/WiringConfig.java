package com.arturkarwowski.testbench.graphql.webflux;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.webflux.domain.DriverFacade;
import com.arturkarwowski.testbench.graphql.webflux.domain.DrivingFineFacade;
import graphql.schema.DataFetcher;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class WiringConfig {

    private DriverFacade driverFacade;
    private DrivingFineFacade drivingFineFacade;

    public WiringConfig(DriverFacade driverFacade, DrivingFineFacade drivingFineFacade) {
        this.driverFacade = driverFacade;
        this.drivingFineFacade = drivingFineFacade;
    }
    DataFetcher<Mono<Optional<Car>>> carFetcher = (environment) -> {

            String licencePlate = environment.getArgument("licencePlate");
            DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

            return Mono.fromFuture(dataLoader.load(licencePlate)).map(Optional::ofNullable);
    };

    DataFetcher<Mono<Driver>> driverFetcher = (environment) -> {

        Car car = environment.getSource();
        return driverFacade.getDriver(car.getDriverId());
    };

    DataFetcher<Mono<List<DrivingFine>>> penaltiesFetcher = (environment) -> {

        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");
        return  Mono.fromFuture(dataLoader.load(driver.getId()));
    };
    DataFetcher<Mono<Integer>> activePenaltyPointsFetcher = (environment) -> {
        Driver driver = environment.getSource();
        DataLoader<String, List<DrivingFine>> dataLoader = environment.getDataLoader("drivingFines");

        var futureFines = dataLoader.load(driver.getId());

        return Mono.fromFuture(futureFines).map(drivingFineFacade::getActivePenaltyPoints);
    };
    DataFetcher<Mono<Optional<Car>>> drivingFineCarFetcher = (environment) -> {
        DrivingFine drivingFine = environment.getSource();
        DataLoader<String, Car> dataLoader = environment.getDataLoader("cars");

        var futureCar = dataLoader.load(drivingFine.getCarLicencePlate());
        return Mono.fromFuture(futureCar).map(Optional::ofNullable);
    };

}
