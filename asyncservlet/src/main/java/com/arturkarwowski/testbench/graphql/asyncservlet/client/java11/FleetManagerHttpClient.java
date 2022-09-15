package com.arturkarwowski.testbench.graphql.asyncservlet.client.java11;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Profile("java11-httpclient")
public class FleetManagerHttpClient implements FleetManagerClient {
    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    private final Executor tracingExecutor;

    public FleetManagerHttpClient(@Value("${http.client.fleet-manager.url}") String baseUrl, Executor tracingExecutor, ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder().executor(tracingExecutor).build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.tracingExecutor = tracingExecutor;
    }

    @Override
    public CompletableFuture<VehiclesResponse> getVehicles(List<String> licencePlates) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("vehicles")
                .queryParam("licencePlate", licencePlates)
                .build().toUri();
        var request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body, tracingExecutor)
                .thenApply(json -> Try.of(()-> objectMapper.readValue(json, VehiclesResponse.class)).get());
    }
}
