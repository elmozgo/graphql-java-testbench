package com.arturkarwowski.testbench.graphql.api.rest;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class CarByLicencePlateDto {

    @JsonUnwrapped
    private Car car;
    private DriverDto driver;


    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public DriverDto getDriver() {
        return driver;
    }

    public void setDriver(DriverDto driver) {
        this.driver = driver;
    }
}
