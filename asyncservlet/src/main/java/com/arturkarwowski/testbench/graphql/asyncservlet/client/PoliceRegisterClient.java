package com.arturkarwowski.testbench.graphql.asyncservlet.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;

import java.util.concurrent.CompletableFuture;

public interface PoliceRegisterClient {

    CompletableFuture<TrafficViolationsResponse> getTrafficViolations(String driverId);
}
