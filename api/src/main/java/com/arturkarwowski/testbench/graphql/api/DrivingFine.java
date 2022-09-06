package com.arturkarwowski.testbench.graphql.api;

import java.time.ZonedDateTime;

public class DrivingFine {

    private final String violationType;
    private final String carLicencePlate;
    private final Integer points;
    private final ZonedDateTime dateTime;

    private DrivingFine(Builder builder) {
        violationType = builder.violationType;
        carLicencePlate = builder.carLicencePlate;
        points = builder.points;
        dateTime = builder.dateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getViolationType() {
        return violationType;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public Integer getPoints() {
        return points;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }


    public static final class Builder {
        private String violationType;
        private String carLicencePlate;
        private Integer points;
        private ZonedDateTime dateTime;

        private Builder() {
        }

        public Builder withViolationType(String val) {
            violationType = val;
            return this;
        }

        public Builder withCarLicencePlate(String val) {
            carLicencePlate = val;
            return this;
        }

        public Builder withPoints(Integer val) {
            points = val;
            return this;
        }

        public Builder withDateTime(ZonedDateTime val) {
            dateTime = val;
            return this;
        }

        public DrivingFine build() {
            return new DrivingFine(this);
        }
    }
}
