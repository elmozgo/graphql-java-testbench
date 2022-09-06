package com.arturkarwowski.testbench.graphql.service.service;

import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DrivingFineServiceTest {

    @Test
    void shouldGetActivePenaltyPoints() {
        var clock = Clock.fixed(Instant.parse("2023-01-10T00:00:00.00Z"), ZoneId.of("UTC"));

        var fines = List.of(
                DrivingFine.builder().withPoints(1).withDateTime(ZonedDateTime.parse("2023-01-09T00:00:00.00Z")).build(),
                DrivingFine.builder().withPoints(2).withDateTime(ZonedDateTime.parse("2023-01-07T00:00:00.00Z")).build(),
                DrivingFine.builder().withPoints(10).withDateTime(ZonedDateTime.parse("2023-01-03T00:00:00.00Z")).build(),
                DrivingFine.builder().withPoints(20).withDateTime(ZonedDateTime.parse("2023-01-05T00:00:00.00Z")).build(),
                DrivingFine.builder().withPoints(100).withDateTime(ZonedDateTime.parse("2023-01-05T00:00:00.01Z")).build());

        var drivingFineService = new DrivingFineService(Duration.parse("P5D"), clock);

        assertThat(drivingFineService.activePenaltyPoints(fines)).isEqualTo(103);
    }

}