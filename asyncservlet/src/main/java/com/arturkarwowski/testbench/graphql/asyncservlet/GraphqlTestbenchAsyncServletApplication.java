package com.arturkarwowski.testbench.graphql.asyncservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.arturkarwowski.testbench.graphql"})
public class GraphqlTestbenchAsyncServletApplication {
    public static void main(String[] args) {

        SpringApplication.run(GraphqlTestbenchAsyncServletApplication.class, args);
    }
}
