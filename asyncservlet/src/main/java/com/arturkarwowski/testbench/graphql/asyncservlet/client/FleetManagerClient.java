package com.arturkarwowski.testbench.graphql.asyncservlet.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FleetManagerClient {

    CompletableFuture<VehiclesResponse> getVehicles(List<String> licencePlates);
}
