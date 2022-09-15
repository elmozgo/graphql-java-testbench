package com.arturkarwowski.testbench.graphql.blockingservlet.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableAsync
public class TraceExecutorConfig {

    private final BeanFactory beanFactory;

    public TraceExecutorConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public Executor tracingExecutor() {
        var threadFactory = new ThreadFactoryBuilder().setNameFormat("async-%d").build();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(threadFactory);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(0);
        executor.initialize();

        return new LazyTraceThreadPoolTaskExecutor(this.beanFactory, executor);
    }

    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    class TracingAsyncConfigurer extends AsyncConfigurerSupport {
        @Override
        public Executor getAsyncExecutor() {
            return tracingExecutor();
        }
    }

}

