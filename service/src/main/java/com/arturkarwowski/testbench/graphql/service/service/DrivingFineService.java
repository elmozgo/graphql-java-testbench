package com.arturkarwowski.testbench.graphql.service.service;

import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DrivingFineService {

    private final Duration pointsExpiration;
    private final Clock clock;

    public DrivingFineService(@Value("${penaltypoints.expiration.duration:365d}") Duration pointsExpiration, Clock clock) {
        this.pointsExpiration = pointsExpiration;
        this.clock = clock;
    }

    public List<DrivingFine> getDrivingFines(List<TrafficViolationDto> trafficViolations) {
        return trafficViolations.stream()
                .map(v -> DrivingFine.builder()
                        .withCarLicencePlate(v.getCarLicencePlate())
                        .withViolationType(v.getViolationType())
                        .withDateTime(v.getDateTime())
                        .withPoints(v.getPoints())
                        .build())
                .collect(Collectors.toList());
    }

    public Integer activePenaltyPoints(List<DrivingFine> drivingFines) {
        return drivingFines.stream().filter(fine ->
                pointsExpiration.compareTo(Duration.between(fine.getDateTime(), ZonedDateTime.now(clock))) > 0)
                .map(DrivingFine::getPoints)
                .reduce(0, Integer::sum);
    }

}
