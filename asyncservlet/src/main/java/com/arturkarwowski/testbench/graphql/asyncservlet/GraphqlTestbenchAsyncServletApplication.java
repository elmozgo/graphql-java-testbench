package com.arturkarwowski.testbench.graphql.asyncservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.arturkarwowski.testbench.graphql"}, exclude = {GraphQlAutoConfiguration.class})
public class GraphqlTestbenchAsyncServletApplication {
    public static void main(String[] args) {

        SpringApplication.run(GraphqlTestbenchAsyncServletApplication.class, args);
    }
}
