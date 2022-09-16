package com.arturkarwowski.testbench.graphql.api.rest;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class DrivingFineDto {

    @JsonUnwrapped
    private DrivingFine drivingFine;
    private Car car;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public DrivingFine getDrivingFine() {
        return drivingFine;
    }

    public void setDrivingFine(DrivingFine drivingFine) {
        this.drivingFine = drivingFine;
    }
}
