package com.arturkarwowski.testbench.graphql.webflux.domain;

import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import com.arturkarwowski.testbench.graphql.service.service.DrivingFineService;
import com.arturkarwowski.testbench.graphql.webflux.client.PoliceRegisterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DrivingFineFacade {

    private static final Logger logger = LoggerFactory.getLogger(DrivingFineFacade.class);
    private final PoliceRegisterClient policeRegisterClient;
    private final DrivingFineService drivingFineService;

    public DrivingFineFacade(PoliceRegisterClient policeRegisterClient, DrivingFineService drivingFineService) {
        this.policeRegisterClient = policeRegisterClient;
        this.drivingFineService = drivingFineService;
    }

    public Mono<List<DrivingFine>> getDrivingFines(String driverId) {
        var response = policeRegisterClient.getTrafficViolations(driverId);

        return response.doOnNext(r -> logger.debug("called police register api for driver id: {}", r.getDriverId()))
                .map(TrafficViolationsResponse::getViolations)
                .map(drivingFineService::getDrivingFines);
    }

    public Integer getActivePenaltyPoints(List<DrivingFine> drivingFines) {
               return drivingFineService.activePenaltyPoints(drivingFines);
    }
}
