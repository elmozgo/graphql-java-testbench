package com.arturkarwowski.testbench.graphql.asyncservlet.client.webclient;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Component
@Profile("webclient")
public class PoliceRegisterWebClient implements PoliceRegisterClient {

    private final WebClient webClient;

    private final Executor tracingExecutor;

    public PoliceRegisterWebClient(WebClient.Builder webClientBuilder, @Value("${http.client.police-register.url}") String baseUrl, Executor tracingExecutor) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.tracingExecutor = tracingExecutor;
    }

    @Override
    public CompletableFuture<TrafficViolationsResponse> getTrafficViolations(String driverId) {

        return webClient.get().uri(uriBuilder -> uriBuilder.path("/driving-fines")
                        .queryParam("driverId", driverId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(TrafficViolationsResponse.class))
                .toFuture()
                .thenApplyAsync(Function.identity(), tracingExecutor);
    }
}
