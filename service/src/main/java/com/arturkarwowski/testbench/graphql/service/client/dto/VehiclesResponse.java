package com.arturkarwowski.testbench.graphql.service.client.dto;

import java.util.List;

public class VehiclesResponse {

    private List<VehicleDto> vehicles;

    public List<VehicleDto> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleDto> vehicles) {
        this.vehicles = vehicles;
    }
}
