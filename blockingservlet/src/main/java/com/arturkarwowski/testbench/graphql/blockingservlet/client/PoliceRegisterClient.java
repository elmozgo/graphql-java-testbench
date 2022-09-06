package com.arturkarwowski.testbench.graphql.blockingservlet.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.TrafficViolationsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "police-register", url="${feign.client.police-register.url}")
public interface PoliceRegisterClient {

    @RequestMapping(method = RequestMethod.GET, value = "/driving-fines")
    TrafficViolationsResponse getTrafficViolations(@RequestParam String driverId);

}
