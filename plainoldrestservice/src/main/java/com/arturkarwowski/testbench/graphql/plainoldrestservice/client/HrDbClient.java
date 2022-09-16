package com.arturkarwowski.testbench.graphql.plainoldrestservice.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "hr-db", url = "${feign.client.hr-db.url}")
public interface HrDbClient {

    @RequestMapping(method = RequestMethod.GET, value = "/employees/{id}")
    EmployeeDto getEmployee(@PathVariable String id);
}
