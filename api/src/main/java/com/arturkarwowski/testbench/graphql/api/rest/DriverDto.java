package com.arturkarwowski.testbench.graphql.api.rest;

import com.arturkarwowski.testbench.graphql.api.Driver;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class DriverDto {

    @JsonUnwrapped
    private Driver driver;
    private Integer penaltyPoints;
    private List<DrivingFineDto> penalties;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Integer getPenaltyPoints() {
        return penaltyPoints;
    }

    public void setPenaltyPoints(Integer penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    public List<DrivingFineDto> getPenalties() {
        return penalties;
    }

    public void setPenalties(List<DrivingFineDto> penalties) {
        this.penalties = penalties;
    }
}
