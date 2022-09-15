package com.arturkarwowski.testbench.graphql.webflux;

import com.arturkarwowski.testbench.graphql.api.Car;
import com.arturkarwowski.testbench.graphql.api.DrivingFine;
import com.arturkarwowski.testbench.graphql.webflux.domain.CarFacade;
import com.arturkarwowski.testbench.graphql.webflux.domain.DrivingFineFacade;
import org.dataloader.CacheMap;
import org.dataloader.DataLoaderOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataloadersConfig {

    private final CarFacade carFacade;
    private final CacheMap<String, Car> carCacheMap;
    private final DrivingFineFacade drivingFineFacade;
    private final CacheMap<String, DrivingFine> drivingFineCacheMap;

    private final BatchLoaderRegistry batchLoaderRegistry;

    public DataloadersConfig(CarFacade carFacade, CacheMap<String, Car> carCacheMap, DrivingFineFacade drivingFineFacade, CacheMap<String, DrivingFine> drivingFineCacheMap, BatchLoaderRegistry batchLoaderRegistry) {
        this.carFacade = carFacade;
        this.carCacheMap = carCacheMap;
        this.drivingFineFacade = drivingFineFacade;
        this.drivingFineCacheMap = drivingFineCacheMap;
        this.batchLoaderRegistry = batchLoaderRegistry;
    }

    @PostConstruct
    public void configureBatchLoaderRegister() {

        var carsDlOptions = DataLoaderOptions.newOptions().setCacheMap(carCacheMap);
        batchLoaderRegistry.<String, Car>forName("cars").withOptions(carsDlOptions).registerMappedBatchLoader( (licencePlates, env) ->
                carFacade.getCars(licencePlates.stream().toList()).map(cars ->
                        cars.stream().collect(Collectors.toMap(Car::getLicencePlate, Function.identity()))));

        var drivingFinesDlOptions = DataLoaderOptions.newOptions().setCacheMap(drivingFineCacheMap);
        batchLoaderRegistry.<String, List<DrivingFine>>forName("drivingFines").withOptions(drivingFinesDlOptions).registerBatchLoader( (driverIds, env) -> {

            var monos  = driverIds.stream().map(drivingFineFacade::getDrivingFines).toList();
            return Flux.fromIterable(monos).flatMap(Function.identity());

        });

    }
}
