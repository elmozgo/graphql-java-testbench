package com.arturkarwowski.testbench.graphql.blockingservlet;

import com.arturkarwowski.testbench.graphql.blockingservlet.config.DataLoaderRegisterBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.GraphQL;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GraphqlController {

    private final GraphQL graphql;
    private final ObjectMapper objectMapper;
    private final DataLoaderRegisterBuilder dataLoaderRegisterBuilder;

    public GraphqlController(GraphQL graphql, ObjectMapper objectMapper, DataLoaderRegisterBuilder dataLoaderRegisterBuilder) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
        this.dataLoaderRegisterBuilder = dataLoaderRegisterBuilder;
    }

    @RequestMapping(value = "/graphql", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> graphqlGET(@RequestParam("query") String query,
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
    public Map<String, Object> graphql(@RequestBody Map<String, Object> body) {
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

    private Map<String, Object> executeGraphqlQuery(String query, String operationName, Map<String, Object> variables) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .dataLoaderRegistry(dataLoaderRegisterBuilder.buildDataLoaderRegistry())
                .operationName(operationName)
                .variables(variables)
                .build();
        return this.graphql.execute(executionInput).toSpecification();
    }

}
