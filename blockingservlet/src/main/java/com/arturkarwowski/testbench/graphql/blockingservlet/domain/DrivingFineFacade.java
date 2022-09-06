package com.arturkarwowski.testbench.graphql.blockingservlet.domain;

import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.blockingservlet.client.PoliceRegisterClient;
import com.arturkarwowski.testbench.graphql.service.service.DrivingFineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public List<DrivingFine> getDrivingFines(String driverId) {
        var response = policeRegisterClient.getTrafficViolations(driverId);
        logger.debug("called police register api");
        return drivingFineService.getDrivingFines(response.getViolations());
    }

    public Integer getActivePenaltyPoints(List<DrivingFine> drivingFines) {
               return drivingFineService.activePenaltyPoints(drivingFines);
    }
}
