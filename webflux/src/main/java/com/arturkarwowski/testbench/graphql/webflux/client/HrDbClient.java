package com.arturkarwowski.testbench.graphql.webflux.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class HrDbClient {

    private final static Logger logger = LoggerFactory.getLogger(HrDbClient.class);

    private final WebClient webClient;
    private final String baseUrl;

    public HrDbClient(WebClient webClient, @Value("${http.client.hr-db.url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public Mono<EmployeeDto> getEmployee(String id) {

        var uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment("employees")
                .pathSegment("{id}")
                .build(id);

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(EmployeeDto.class));
    }
}
