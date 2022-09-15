package com.arturkarwowski.testbench.graphql.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.arturkarwowski.testbench.graphql"})
public class GraphqlTestbenchWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlTestbenchWebfluxApplication.class, args);
	}

}
