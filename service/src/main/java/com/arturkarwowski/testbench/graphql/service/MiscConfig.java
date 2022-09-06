package com.arturkarwowski.testbench.graphql.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class MiscConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
