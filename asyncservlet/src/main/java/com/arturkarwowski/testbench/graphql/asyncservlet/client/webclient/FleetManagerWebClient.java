package com.arturkarwowski.testbench.graphql.asyncservlet.client.webclient;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Component
@Profile("webclient")
public class FleetManagerWebClient implements FleetManagerClient {

    private static final Logger logger = LoggerFactory.getLogger(FleetManagerWebClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final Executor executor;

    public FleetManagerWebClient(WebClient webClient, @Value("${http.client.fleet-manager.url}") String baseUrl, Executor executor) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.executor = executor;
    }
    @Override
    public CompletableFuture<VehiclesResponse> getVehicles(List<String> licencePlates) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("vehicles")
                .queryParam("licencePlate", licencePlates)
                .build().toUri();

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(VehiclesResponse.class))
                .toFuture()
                .thenApply(r -> {logger.debug("got response"); return r;})
                .thenApplyAsync(Function.identity(), executor);
    }
}
