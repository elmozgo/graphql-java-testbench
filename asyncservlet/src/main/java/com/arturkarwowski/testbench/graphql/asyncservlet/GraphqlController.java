package com.arturkarwowski.testbench.graphql.asyncservlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class GraphqlController {

    private static final Logger logger = LoggerFactory.getLogger(GraphqlController.class);
    private final GraphQL graphql;
    private final ObjectMapper objectMapper;

    private final DataLoaderRegistry dataLoaderRegistry;

    @Autowired
    public GraphqlController(GraphQL graphql, ObjectMapper objectMapper, DataLoaderRegistry dataLoaderRegistry) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
        this.dataLoaderRegistry = dataLoaderRegistry;
    }

    @RequestMapping(value = "/graphql", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Object>> graphqlGET(@RequestParam("query") String query,
                                          @RequestParam(value = "operationName", required = false) String operationName,
                                          @RequestParam("variables") String variablesJson
    ) throws IOException {
        Map<String, Object> variables = new LinkedHashMap<>();
        if (variablesJson != null) {
            variables = objectMapper.readValue(variablesJson, new TypeReference<Map<String, Object>>() {
            });
        }
        return executeGraphqlQuery(query, operationName, variables);
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/graphql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Object>> graphql(@RequestBody Map<String, Object> body) {
        String query = (String) body.get("query");
        if (query == null) {
            query = "";
        }
        String operationName = (String) body.get("operationName");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        return executeGraphqlQuery(query, operationName, variables);
    }

    private CompletableFuture<Map<String, Object>> executeGraphqlQuery(String query, String operationName, Map<String, Object> variables) {

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .dataLoaderRegistry(dataLoaderRegistry)
                .operationName(operationName)
                .variables(variables)
                .build();
        return this.graphql.executeAsync(executionInput).thenApply(ExecutionResult::toSpecification);
    }

}
