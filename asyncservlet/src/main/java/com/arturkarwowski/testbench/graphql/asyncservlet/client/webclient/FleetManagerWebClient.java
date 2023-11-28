package com.arturkarwowski.testbench.graphql.asyncservlet.client.webclient;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Component
@Profile("webclient")
public class FleetManagerWebClient implements FleetManagerClient {

    private static final Logger logger = LoggerFactory.getLogger(FleetManagerWebClient.class);

    @Autowired
    private Tracer tracer;

    @Configuration
    static class Config {
        @Bean
        WebClient webClientForFleetManager(WebClient.Builder builder,  @Value("${http.client.fleet-manager.url}") String baseUrl) {
            return builder.baseUrl(baseUrl).build();
        }
    }

    private final WebClient webClient;

    private final Executor tracingExecutor;

    public FleetManagerWebClient(WebClient webClientForFleetManager, Executor tracingExecutor) {
        this.webClient = webClientForFleetManager;
        this.tracingExecutor = tracingExecutor;
    }

    @Override
    public CompletableFuture<VehiclesResponse> getVehicles(List<String> licencePlates) {

        logger.debug(tracer.currentTraceContext().context().traceId());

        return webClient.get().uri(uriBuilder ->
                        uriBuilder.path("/vehicles")
                                .queryParam("licencePlate", licencePlates)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(VehiclesResponse.class))
                .toFuture()
                .thenApply(r -> {
                    logger.debug("got response");
                    return r;
                })
                .thenApplyAsync(Function.identity(), tracingExecutor);
    }
}
