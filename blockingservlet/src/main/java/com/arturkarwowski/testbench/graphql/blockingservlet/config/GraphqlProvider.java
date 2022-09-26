package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.GraphQL;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
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
    private final String schemaSdl;

    private final RuntimeWiring runtimeWiring;

    private GraphQL graphQL;

    public GraphqlProvider(String schemaSdl, RuntimeWiring runtimeWiring) {
        this.schemaSdl = schemaSdl;
        this.runtimeWiring = runtimeWiring;
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
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }


    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }
}
