package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;

import brave.http.HttpTracing;
import brave.httpasyncclient.TracingHttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("apache-hc-client4")
public class TracingConfiguration {

    @Bean
    HttpAsyncClientBuilder traceHttpAsyncClientBuilder(HttpTracing httpTracing) {
        return TracingHttpAsyncClientBuilder.create(httpTracing);
    }
}
