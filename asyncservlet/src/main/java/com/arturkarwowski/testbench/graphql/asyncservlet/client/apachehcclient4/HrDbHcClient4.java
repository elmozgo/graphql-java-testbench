package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Profile("apache-hc-client4")
public class HrDbHcClient4 implements HrDbClient {

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public HrDbHcClient4(@Value("${http.client.hr-db.url}") String baseUrl, HttpAsyncClientBuilder httpAsyncClientBuilder, ObjectMapper objectMapper) {
        this.httpAsyncClient = httpAsyncClientBuilder.build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
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
                .thenApply(HttpResponse::getEntity)
                .thenApply(e -> Try.of(()-> objectMapper.readValue(e.getContent(), EmployeeDto.class)).get());
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        httpAsyncClient.close();
    }
}
