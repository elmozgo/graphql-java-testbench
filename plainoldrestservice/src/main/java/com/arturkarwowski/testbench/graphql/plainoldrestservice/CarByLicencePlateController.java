package com.arturkarwowski.testbench.graphql.plainoldrestservice;

import com.arturkarwowski.testbench.graphql.api.rest.CarByLicencePlateDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CarByLicencePlateController {

    private final CarByLicencePlateFacade carByLicencePlateFacade;

    public CarByLicencePlateController(CarByLicencePlateFacade carByLicencePlateFacade) {
        this.carByLicencePlateFacade = carByLicencePlateFacade;
    }

    @GetMapping("/car/licence-plate/{licencePlate}")
    public Optional<CarByLicencePlateDto> getCar(@PathVariable String licencePlate) {
        return carByLicencePlateFacade.getCar(licencePlate);
    }
}
