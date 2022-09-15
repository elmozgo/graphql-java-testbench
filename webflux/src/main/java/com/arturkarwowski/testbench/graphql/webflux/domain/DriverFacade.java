package com.arturkarwowski.testbench.graphql.webflux.domain;

import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.webflux.client.HrDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DriverFacade {

    private static final Logger logger = LoggerFactory.getLogger(DriverFacade.class);
    private final HrDbClient hrDbClient;

    public DriverFacade(HrDbClient hrDbClient) {
        this.hrDbClient = hrDbClient;
    }

    public Mono<Driver> getDriver(String id) {

        var employee = hrDbClient.getEmployee(id);

        return employee.doOnNext(e -> logger.debug("called hr db api for employee id {}", e.getId()))
                .map(e -> new Driver(e.getId(), e.getFirstName(), e.getLastName()));
    }

}
