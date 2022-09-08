package com.arturkarwowski.testbench.graphql.asyncservlet.client;

import com.arturkarwowski.testbench.graphql.service.client.dto.EmployeeDto;

import java.util.concurrent.CompletableFuture;

public interface HrDbClient {

    CompletableFuture<EmployeeDto> getEmployee(String id);
}
