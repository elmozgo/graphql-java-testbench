package com.arturkarwowski.testbench.graphql.service.client.dto;

import java.util.List;

public class TrafficViolationsResponse {

    private String driverId;
    private List<TrafficViolationDto> violations;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public List<TrafficViolationDto> getViolations() {
        return violations;
    }

    public void setViolations(List<TrafficViolationDto> violations) {
        this.violations = violations;
    }
}
