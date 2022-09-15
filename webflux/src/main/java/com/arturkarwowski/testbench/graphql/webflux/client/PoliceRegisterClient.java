package com.arturkarwowski.testbench.graphql.webflux.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class PoliceRegisterClient {

    private final WebClient webClient;

    private final String baseUrl;

    public PoliceRegisterClient(WebClient webClient, @Value("${http.client.police-register.url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public Mono<TrafficViolationsResponse> getTrafficViolations(String driverId) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("driving-fines")
                .queryParam("driverId", driverId)
                .build().toUri();

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(TrafficViolationsResponse.class));
    }
}
