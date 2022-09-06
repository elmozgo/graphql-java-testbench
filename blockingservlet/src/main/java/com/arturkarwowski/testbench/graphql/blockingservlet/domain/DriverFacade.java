package com.arturkarwowski.testbench.graphql.blockingservlet.domain;

import com.arturkarwowski.testbench.graphql.api.Driver;
import com.arturkarwowski.testbench.graphql.blockingservlet.client.HrDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DriverFacade {

    private static final Logger logger = LoggerFactory.getLogger(DriverFacade.class);
    private final HrDbClient hrDbClient;

    public DriverFacade(HrDbClient hrDbClient) {
        this.hrDbClient = hrDbClient;
    }

    public Driver getDriver(String id) {

        var employee = hrDbClient.getEmployee(id);
        logger.debug("called hr db api");
        return new Driver(employee.getId(), employee.getFirstName(), employee.getLastName());
    }

}
