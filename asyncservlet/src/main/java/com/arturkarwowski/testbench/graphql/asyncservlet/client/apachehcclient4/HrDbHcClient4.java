package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
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

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Profile("apache-hc-client4")
public class HrDbHcClient4 implements HrDbClient {

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    private final Executor executor;

    public HrDbHcClient4(@Value("${http.client.hr-db.url}") String baseUrl, HttpAsyncClientBuilder httpAsyncClientBuilder, ObjectMapper objectMapper, Executor executor) throws IOReactorException {
        IOReactorConfig reactorConfig = IOReactorConfig.custom().build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(reactorConfig);
        PoolingNHttpClientConnectionManager cm =
                new PoolingNHttpClientConnectionManager(ioReactor);

        this.httpAsyncClient = httpAsyncClientBuilder.setConnectionManager(cm).build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.executor = executor;
        this.httpAsyncClient.start();
    }

    @Override
    public CompletableFuture<EmployeeDto> getEmployee(String id) {
        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("employees")
                .pathSegment("{id}")
                .build(id);
        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-Type", "application/json");

        return CompletableFuturisationUtils.toCompletableFuture(callback ->
                        httpAsyncClient.execute(request, callback))
                .thenApplyAsync(HttpResponse::getEntity, executor)
                .thenApply(e -> Try.of(()-> objectMapper.readValue(e.getContent(), EmployeeDto.class)).get());
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        httpAsyncClient.close();
    }
}
