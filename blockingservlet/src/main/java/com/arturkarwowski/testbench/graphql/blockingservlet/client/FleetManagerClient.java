package com.arturkarwowski.testbench.graphql.blockingservlet.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.VehiclesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "fleet-manager", url = "${feign.client.fleet-manager.url}")
public interface FleetManagerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/vehicles")
    VehiclesResponse getVehicles(@RequestParam("licencePlate") List<String> licencePlates);
}
