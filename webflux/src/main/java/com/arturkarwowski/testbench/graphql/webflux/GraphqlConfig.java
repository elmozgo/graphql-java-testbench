package com.arturkarwowski.testbench.graphql.webflux;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.scalars.ExtendedScalars;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.function.Function;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration(proxyBeanMethods = false)
public class GraphqlConfig {

    private final DataFetcherDefinitions dataFetcherDefinitions;

    public GraphqlConfig(DataFetcherDefinitions dataFetcherDefinitions) {
        this.dataFetcherDefinitions = dataFetcherDefinitions;
    }

    private PreparsedDocumentProvider buildPreparsedDocumentProvider() {
        Cache<String, PreparsedDocumentEntry> cache = Caffeine.newBuilder().maximumSize(100).build();

        return (executionInput, computeFunction) -> {
            Function<String, PreparsedDocumentEntry> mapCompute = key -> computeFunction.apply(executionInput);
            return cache.get(executionInput.getQuery(), mapCompute);
        };
    }

    @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer(Resource schemaResource) {
        return (builder) -> builder
                .configureGraphQl(graphQlBuilder -> graphQlBuilder
                        .preparsedDocumentProvider(buildPreparsedDocumentProvider()))
                .configureRuntimeWiring(configurer -> configurer
                        .type(newTypeWiring("Query")
                                .dataFetcher("carByLicencePlate", dataFetcherDefinitions.carFetcher)
                                .build())
                        .type(newTypeWiring("Car")
                                .dataFetcher("driver", dataFetcherDefinitions.driverFetcher)
                                .build())
                        .type(newTypeWiring("Driver")
                                .dataFetcher("activePenaltyPoints", dataFetcherDefinitions.activePenaltyPointsFetcher)
                                .dataFetcher("penalties", dataFetcherDefinitions.penaltiesFetcher)
                                .build())
                        .type(newTypeWiring("DrivingFine")
                                .dataFetcher("car", dataFetcherDefinitions.drivingFineCarFetcher)
                                .build())
                        .scalar(ExtendedScalars.DateTime)
                        .build())
                .build();
    }

}
