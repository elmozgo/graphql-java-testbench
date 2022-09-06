package com.arturkarwowski.testbench.graphql.service.service;

import org.springframework.stereotype.Component;

@Component
public class DescriptionSanitiser {

    public String sanitise(String description) {

        return description.replaceAll("(?i)lorem", "pain").toUpperCase();
    }

}
