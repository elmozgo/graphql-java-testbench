package com.arturkarwowski.testbench.graphql.webflux;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.dataloader.CacheMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Configuration
public class CacheMapsConfig {

    @Bean
    public CacheMap<String, Car> carDataloaderCacheMap() {

        Cache<String, CompletableFuture<Car>> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build();

        return wrapWithCacheMap(cache);
    }

    @Bean
    public CacheMap<String, DrivingFine> drivingFineDataloaderCacheMap() {

        Cache<String, CompletableFuture<DrivingFine>> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build();

        return wrapWithCacheMap(cache);
    }

    private <K,V> CacheMap<K, V> wrapWithCacheMap(Cache<K, CompletableFuture<V>> cache) {

        return new CacheMap<>() {
            @Override
            public boolean containsKey(K key) {
                return cache.getIfPresent(key) != null;
            }

            @Override
            public CompletableFuture<V> get(K key) {
                return cache.getIfPresent(key);
            }

            @Override
            public CacheMap<K, V> set(K key, CompletableFuture<V> value) {
                cache.put(key, value);
                return this;
            }

            @Override
            public CacheMap<K, V> delete(K key) {
                cache.invalidate(key);
                return this;
            }

            @Override
            public CacheMap<K, V> clear() {
                cache.invalidateAll();
                return this;
            }
        };
    }
}
