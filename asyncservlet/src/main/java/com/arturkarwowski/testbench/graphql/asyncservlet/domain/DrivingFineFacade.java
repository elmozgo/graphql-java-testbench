package com.arturkarwowski.testbench.graphql.asyncservlet.domain;

import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.asyncservlet.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import com.arturkarwowski.testbench.graphql.service.service.DrivingFineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class DrivingFineFacade {

    private static final Logger logger = LoggerFactory.getLogger(DrivingFineFacade.class);
    private final PoliceRegisterClient policeRegisterClient;
    private final DrivingFineService drivingFineService;

    public DrivingFineFacade(PoliceRegisterClient policeRegisterClient, DrivingFineService drivingFineService) {
        this.policeRegisterClient = policeRegisterClient;
        this.drivingFineService = drivingFineService;
    }

    public CompletableFuture<List<DrivingFine>> getDrivingFines(String driverId) {
        var response = policeRegisterClient.getTrafficViolations(driverId);

        response.thenRun(() -> logger.debug("called police register api"));
        return response.thenApply(TrafficViolationsResponse::getViolations).thenApply(drivingFineService::getDrivingFines);
    }

    public Integer getActivePenaltyPoints(List<DrivingFine> drivingFines) {
               return drivingFineService.activePenaltyPoints(drivingFines);
    }
}
