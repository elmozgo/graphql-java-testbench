package com.arturkarwowski.testbench.graphql.plainoldrestservice;

import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.api.rest.CarByLicencePlateDto;
import com.arturkarwowski.testbench.graphql.api.rest.DriverDto;
import com.arturkarwowski.testbench.graphql.api.rest.DrivingFineDto;
import com.arturkarwowski.testbench.graphql.plainoldrestservice.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.plainoldrestservice.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.plainoldrestservice.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehicleDto;
import com.arturkarwowski.testbench.graphql.service.service.CarService;
import com.arturkarwowski.testbench.graphql.service.service.DrivingFineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CarByLicencePlateFacade {

    private final FleetManagerClient fleetManagerClient;
    private final HrDbClient hrDbClient;
    private final PoliceRegisterClient policeRegisterClient;

    private final CarService carService;
    private final DrivingFineService drivingFineService;


    public CarByLicencePlateFacade(FleetManagerClient fleetManagerClient,
                                   HrDbClient hrDbClient,
                                   PoliceRegisterClient policeRegisterClient,
                                   CarService carService,
                                   DrivingFineService drivingFineService) {
        this.fleetManagerClient = fleetManagerClient;
        this.hrDbClient = hrDbClient;
        this.policeRegisterClient = policeRegisterClient;
        this.carService = carService;
        this.drivingFineService = drivingFineService;
    }

    public Optional<CarByLicencePlateDto> getCar(String licencePlate) {

        var vehicle = fleetManagerClient.getVehicles(List.of(licencePlate)).getVehicles().stream().findFirst();

        if(vehicle.isPresent()) {
            var carByLicencePlate = new CarByLicencePlateDto();
            var car = carService.buildFrom(vehicle.get());
            carByLicencePlate.setCar(car);

            DriverDto driver = getDriverDto(vehicle.get());
            carByLicencePlate.setDriver(driver);

            return Optional.of(carByLicencePlate);
        }

        return Optional.empty();

    }

    private DriverDto getDriverDto(VehicleDto vehicle) {
        var driver = new DriverDto();
        var employee = hrDbClient.getEmployee(vehicle.getDriverId());
        driver.setDriver(new Driver(employee.getId(), employee.getFirstName(), employee.getLastName()));

        var violations= policeRegisterClient.getTrafficViolations(employee.getId()).getViolations();
        var drivingFines = drivingFineService.getDrivingFines(violations);
        var activePenaltyPoints = drivingFineService.activePenaltyPoints(drivingFines);

        driver.setPenalties(drivingFines.stream().map(this::getDrivingFineDto).collect(Collectors.toList()));
        driver.setPenaltyPoints(activePenaltyPoints);
        return driver;
    }

    private DrivingFineDto getDrivingFineDto(DrivingFine fine) {
        var dto = new DrivingFineDto();
        dto.setDrivingFine(fine);
        var car = fleetManagerClient.getVehicles(List.of(fine.getCarLicencePlate())).getVehicles().stream().findFirst().map(carService::buildFrom);
        car.ifPresent(dto::setCar);
        return dto;
    }


}
