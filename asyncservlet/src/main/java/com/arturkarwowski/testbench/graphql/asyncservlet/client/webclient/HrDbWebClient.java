package com.arturkarwowski.testbench.graphql.asyncservlet.client.webclient;

import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Component
@Profile("webclient")
public class HrDbWebClient implements HrDbClient {

    private final static Logger logger = LoggerFactory.getLogger(HrDbWebClient.class);

    @Autowired
    private Tracer tracer;

    @Configuration
    static class Config {
        @Bean
        WebClient webClientForHrDb(WebClient.Builder builder,  @Value("${http.client.hr-db.url}") String baseUrl) {
            return builder.baseUrl(baseUrl).build();
        }
    }

    private final WebClient webClient;

    private final Executor tracingExecutor;

    public HrDbWebClient(WebClient webClientForHrDb, Executor tracingExecutor) {
        this.webClient = webClientForHrDb;
        this.tracingExecutor = tracingExecutor;
    }

    @Override
    public CompletableFuture<EmployeeDto> getEmployee(String id) {

        logger.debug(tracer.currentTraceContext().context().traceId());

        return webClient.get().uri(uriBuilder -> uriBuilder.path("/employees/{id}").build(id))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(EmployeeDto.class))
                .toFuture()
                .thenApplyAsync(Function.identity(), tracingExecutor);
    }
}
