package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.FleetManagerClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.HttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Profile("apache-hc-client4")
public class FleetManagerHcClient4 implements FleetManagerClient {

    private final static Logger logger = LoggerFactory.getLogger(FleetManagerHcClient4.class);

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public FleetManagerHcClient4(@Value("${http.client.fleet-manager.url}") String baseUrl, HttpAsyncClientBuilder httpAsyncClientBuilder, ObjectMapper objectMapper) {
        this.httpAsyncClient = httpAsyncClientBuilder.build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.httpAsyncClient.start();
    }

    @Override
    public CompletableFuture<VehiclesResponse> getVehicles(List<String> licencePlates) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("vehicles")
                .queryParam("licencePlate", licencePlates)
                .build().toUri();

        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-Type", "application/json");

        return CompletableFuturisationUtils.toCompletableFuture(callback ->
                httpAsyncClient.execute(request, callback))
                .thenApply(b -> {logger.debug("received response"); return b;})
                .thenApply(HttpResponse::getEntity)
                .thenApply(e -> Try.of(()-> objectMapper.readValue(e.getContent(), VehiclesResponse.class)).get());
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        httpAsyncClient.close();
    }

}
