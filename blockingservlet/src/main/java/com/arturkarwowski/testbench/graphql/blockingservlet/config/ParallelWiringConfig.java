package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.Executor;

import static graphql.schema.AsyncDataFetcher.async;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
@Profile("parallel")
public class ParallelWiringConfig {
    private final DataFetcherDefinitions dataFetcherDefinitions;
    private final Executor tracingExecutor;

    public ParallelWiringConfig(DataFetcherDefinitions dataFetcherDefinitions, Executor tracingExecutor) {
        this.dataFetcherDefinitions = dataFetcherDefinitions;
        this.tracingExecutor = tracingExecutor;
    }

    @Bean
    public RuntimeWiring runtimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("carByLicencePlate", async(dataFetcherDefinitions.carFetcher, tracingExecutor))
                        .build())
                .type(newTypeWiring("Car")
                        .dataFetcher("driver", async(dataFetcherDefinitions.driverFetcher, tracingExecutor))
                        .build())
                .type(newTypeWiring("Driver")
                        .dataFetcher("activePenaltyPoints", async(dataFetcherDefinitions.activePenaltyPointsFetcher, tracingExecutor))
                        .dataFetcher("penalties", async(dataFetcherDefinitions.penaltiesFetcher, tracingExecutor))
                        .build())
                .type(newTypeWiring("DrivingFine")
                        .dataFetcher("car", async(dataFetcherDefinitions.drivingFineCarFetcher, tracingExecutor))
                        .build())
                .scalar(ExtendedScalars.DateTime)
                .build();
    }
}
