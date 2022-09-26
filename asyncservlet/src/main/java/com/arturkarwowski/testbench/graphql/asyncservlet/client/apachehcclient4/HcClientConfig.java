package com.arturkarwowski.testbench.graphql.asyncservlet.client.apachehcclient4;


import brave.http.HttpTracing;
import brave.httpasyncclient.TracingHttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("apache-hc-client4")
public class HcClientConfig {

    @Bean
    public IOReactorConfig ioReactorConfig() {
        return IOReactorConfig.custom().setIoThreadCount(200).build();
    }

    @Bean
    public HttpAsyncClientBuilder traceHttpAsyncClientBuilder(HttpTracing httpTracing) {
        return TracingHttpAsyncClientBuilder.create(httpTracing);
    }

}
