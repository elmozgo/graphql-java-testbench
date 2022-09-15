package com.arturkarwowski.testbench.graphql.webflux.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class FleetManagerClient {

    private static final Logger logger = LoggerFactory.getLogger(FleetManagerClient.class);

    private final WebClient webClient;
    private final String baseUrl;

    public FleetManagerClient(WebClient webClient, @Value("${http.client.fleet-manager.url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public Mono<VehiclesResponse> getVehicles(List<String> licencePlates) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("vehicles")
                .queryParam("licencePlate", licencePlates)
                .build().toUri();

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(VehiclesResponse.class));
    }
}
