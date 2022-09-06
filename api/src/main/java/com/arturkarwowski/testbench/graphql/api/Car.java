package com.arturkarwowski.testbench.graphql.api;

public class Car {
    private final String licencePlate;
    private final String description;
    private final String driverId;

    public Car(String licencePlate, String driverId, String description) {
        this.licencePlate = licencePlate;
        this.description = description;
        this.driverId = driverId;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public String getDescription() {
        return description;
    }

    public String getDriverId() {
        return driverId;
    }
}
