package com.arturkarwowski.testbench.graphql.blockingservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.arturkarwowski.testbench.graphql"})
@EnableFeignClients
public class GraphqlTestbenchBlockingServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlTestbenchBlockingServletApplication.class, args);
	}

}
