package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
@Profile("sequential")
public class SequentialWiringConfig {
    private final DataFetcherDefinitions dataFetcherDefinitions;

    public SequentialWiringConfig(DataFetcherDefinitions dataFetcherDefinitions) {
        this.dataFetcherDefinitions = dataFetcherDefinitions;
    }

    @Bean
    public RuntimeWiring runtimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
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
                .build();
    }
}
