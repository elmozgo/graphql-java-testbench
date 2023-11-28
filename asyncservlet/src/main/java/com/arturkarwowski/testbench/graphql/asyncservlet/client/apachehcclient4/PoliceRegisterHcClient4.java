package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Profile("apache-hc-client4")
public class PoliceRegisterHcClient4 implements PoliceRegisterClient {

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    private final Executor tracingExecutor;

    public PoliceRegisterHcClient4(@Value("${http.client.police-register.url}") String baseUrl,
                                   HttpAsyncClientBuilder httpAsyncClientBuilder,
                                   ObjectMapper objectMapper,
                                   IOReactorConfig reactorConfig,
                                   Executor tracingExecutor) throws IOReactorException {
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(reactorConfig);
        PoolingNHttpClientConnectionManager cm =
                new PoolingNHttpClientConnectionManager(ioReactor);

        this.httpAsyncClient = httpAsyncClientBuilder.setConnectionManager(cm).build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.tracingExecutor = tracingExecutor;
        this.httpAsyncClient.start();

    }
    @Override
    public CompletableFuture<TrafficViolationsResponse> getTrafficViolations(String driverId) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("driving-fines")
                .queryParam("driverId", driverId)
                .build().toUri();

        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-Type", "application/json");

        return CompletableFuturisationUtils.toCompletableFuture(callback ->
                        httpAsyncClient.execute(request, callback))
                .thenApplyAsync(HttpResponse::getEntity, tracingExecutor)
                .thenApply(e -> Try.of(()-> objectMapper.readValue(e.getContent(), TrafficViolationsResponse.class)).get());
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        httpAsyncClient.close();
    }
}
