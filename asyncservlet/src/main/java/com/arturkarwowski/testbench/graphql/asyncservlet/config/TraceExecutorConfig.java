package com.arturkarwowski.testbench.graphql.asyncservlet.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextScheduledExecutorService;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class TraceExecutorConfig {

    private final BeanFactory beanFactory;

    public TraceExecutorConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

//    @Bean
//    TaskDecorator traceTaskDecorator() {
////        return new ContextPropagatingTaskDecorator();
//        return (runnable) -> ContextSnapshotFactory.builder().build().captureAll().wrap(runnable);
//    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor tracingExecutor() {
        var threadFactory = new ThreadFactoryBuilder().setNameFormat("async-%d").build();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
            @Override
            protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                ExecutorService executorService = super.initializeExecutor(threadFactory, rejectedExecutionHandler);
                return ContextExecutorService.wrap(executorService, () -> ContextSnapshotFactory.builder().build().captureAll());
            }
        };

        executor.setThreadFactory(threadFactory);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setQueueCapacity(0);
        executor.initialize();

        return executor;

//        return new ThreadPoolTaskExecutorBuilder()
//                .corePoolSize(5)
//                .maxPoolSize(Integer.MAX_VALUE)
//                .queueCapacity(0)
//                .threadNamePrefix("async-")
//                .taskDecorator(traceTaskDecorator)
//                .build();

//        return executor;
//            ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler() {
//                @Override
//                protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
//                    ExecutorService executorService = super.initializeExecutor(threadFactory, rejectedExecutionHandler);
//                    return ContextExecutorService.wrap(executorService, ContextSnapshot::captureAll);
//                }
//
//
//                @Override
//                public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
//                    return ContextScheduledExecutorService.wrap(super.getScheduledExecutor());
//                }
//            };
//            threadPoolTaskScheduler.initialize();
//            return threadPoolTaskScheduler;
    }

    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @EnableAsync
    static class TracingAsyncConfigurer implements AsyncConfigurer, WebMvcConfigurer {

        private final ThreadPoolTaskExecutor taskExecutor;

        TracingAsyncConfigurer(ThreadPoolTaskExecutor taskExecutor) {
            this.taskExecutor = taskExecutor;
        }

        @Override
        public Executor getAsyncExecutor() {
            return taskExecutor;
        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setTaskExecutor(taskExecutor);
        }
//        @Override
//        public Executor getAsyncExecutor() {
//            return ContextExecutorService.wrap(Executors.newCachedThreadPool(), ContextSnapshot::captureAll);
//        }
//
//        @Override
//        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//            configurer.setTaskExecutor(new SimpleAsyncTaskExecutor(r -> new Thread(ContextSnapshotFactory.builder().build().captureAll().wrap(r))));
//        }
    }


}

