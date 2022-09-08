package com.arturkarwowski.testbench.graphql.asyncservlet.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.GraphQL;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Function;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphqlProvider {
    private final WiringConfig wiringConfig;
    private final String schemaSdl;

    private GraphQL graphQL;

    public GraphqlProvider(WiringConfig wiringConfig, String schemaSdl) {
        this.wiringConfig = wiringConfig;
        this.schemaSdl = schemaSdl;
    }

    @PostConstruct
    public void init() {
        GraphQLSchema graphQLSchema = buildSchema(schemaSdl);

        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .preparsedDocumentProvider(buildPreparsedDocumentProvider())
                .build();
    }

    private PreparsedDocumentProvider buildPreparsedDocumentProvider() {
        Cache<String, PreparsedDocumentEntry> cache = Caffeine.newBuilder().maximumSize(100).build();

        return (executionInput, computeFunction) -> {
            Function<String, PreparsedDocumentEntry> mapCompute = key -> computeFunction.apply(executionInput);
            return cache.get(executionInput.getQuery(), mapCompute);
        };
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("carByLicencePlate", wiringConfig.carFetcher)
                        .build())
                .type(newTypeWiring("Car")
                        .dataFetcher("driver", wiringConfig.driverFetcher)
                        .build())
                .type(newTypeWiring("Driver")
                        .dataFetcher("activePenaltyPoints", wiringConfig.activePenaltyPointsFetcher)
                        .dataFetcher("penalties", wiringConfig.penaltiesFetcher)
                        .build())
                .type(newTypeWiring("DrivingFine")
                        .dataFetcher("car", wiringConfig.drivingFineCarFetcher)
                        .build())
                .scalar(ExtendedScalars.DateTime)
                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }
}
