package com.arturkarwowski.testbench.graphql.asyncservlet.domain;

import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.asyncservlet.client.HrDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DriverFacade {

    private static final Logger logger = LoggerFactory.getLogger(DriverFacade.class);
    private final HrDbClient hrDbClient;

    public DriverFacade(HrDbClient hrDbClient) {
        this.hrDbClient = hrDbClient;
    }

    public CompletableFuture<Driver> getDriver(String id) {

        var employee = hrDbClient.getEmployee(id);

        employee.thenRun(() -> logger.debug("called hr db api"));
        return employee.thenApply(e -> new Driver(e.getId(), e.getFirstName(), e.getLastName()));
    }

}
