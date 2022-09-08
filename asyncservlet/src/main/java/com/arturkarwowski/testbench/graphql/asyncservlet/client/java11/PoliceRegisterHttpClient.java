package com.arturkarwowski.testbench.graphql.asyncservlet.client.java11;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Profile("java11-httpclient")
public class PoliceRegisterHttpClient implements PoliceRegisterClient {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public PoliceRegisterHttpClient(@Value("${http.client.police-register.url}") String baseUrl, Executor executor, ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder().executor(executor).build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<TrafficViolationsResponse> getTrafficViolations(String driverId) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("driving-fines")
                .queryParam("driverId", driverId)
                .build().toUri();
        var request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> Try.of(()-> objectMapper.readValue(json, TrafficViolationsResponse.class)).get());
    }
}
