package com.arturkarwowski.testbench.graphql.asyncservlet.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableAsync
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class TraceExecutorConfig extends AsyncConfigurerSupport {

    private final BeanFactory beanFactory;

    public TraceExecutorConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        return new LazyTraceExecutor(this.beanFactory, executor);
    }

}

