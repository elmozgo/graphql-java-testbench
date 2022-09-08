package com.arturkarwowski.testbench.graphql.asyncservlet.client.java11;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HrDbHttpClient implements HrDbClient {

    private final static Logger logger = LoggerFactory.getLogger(HrDbHttpClient.class);

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    private final Executor executor;

    public HrDbHttpClient(@Value("${http.client.hr-db.url}") String baseUrl, Executor executor, ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder().executor(executor).build();
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<EmployeeDto> getEmployee(String id) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("employees")
                .pathSegment("{id}")
                .build(id);
        var request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        logger.debug("about to fire a request");

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body, executor)
                .thenApply(b -> {logger.debug("received response"); return b;})
                .thenApply(json -> Try.of(()-> objectMapper.readValue(json, EmployeeDto.class)).get());
    }
}
