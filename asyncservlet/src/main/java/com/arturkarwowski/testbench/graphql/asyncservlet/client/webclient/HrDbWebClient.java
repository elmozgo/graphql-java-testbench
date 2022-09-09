package com.arturkarwowski.testbench.graphql.asyncservlet.client.webclient;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HrDbWebClient implements HrDbClient {

    private final static Logger logger = LoggerFactory.getLogger(HrDbWebClient.class);

    private final WebClient webClient;
    private final String baseUrl;

    private final Executor executor;

    public HrDbWebClient(WebClient webClient, @Value("${http.client.hr-db.url}") String baseUrl, Executor executor) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<EmployeeDto> getEmployee(String id) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("employees")
                .pathSegment("{id}")
                .build(id);

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(EmployeeDto.class))
                .toFuture()
                .thenApplyAsync(Function.identity(), executor);
    }
}
