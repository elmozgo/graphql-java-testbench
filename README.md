# graphql-java test bench

<table>
    <tr>
        <td>
            <p>The goal of this project is to test, play and make experiments with various ways of using the <strong>graphql-java</strong> engine in a <strong>Spring Boot</strong> app.</p>
            <p>The simulated role of the service under test is a "domain aggregator", "frontend backend" or "gateway" - a service that calls downstream apis and processes their response in order to prepare and serve a unified model for an upstream website or report render.</p>
        </td>
        <td width="30%">
            <img src="f16_engine_pw_f100.jpg" alt="PW F100 engine during testing" width="300"/>
        </td>
    </tr>
</table>


## scenario

The service under test it used to get all data that is required to print multiple reports (ranging in granularity) of a company car.

The *Fleet Manager* keeps data about cars in the fleet, The *HR Database* holds personal information of the assigned drivers and the integration with the *Police Register* provides us with some insight on the driving style and skill of the employees.

```mermaid
flowchart LR;
    graphql[graphql<br>service];
    start((client)) -- "carByLicencePlate()" --> graphql
    subgraph downstream
        fm["Fleet Manager"];
        hrdb["HR Database"];
        pr["Police Register"];
    end
    graphql--"GET /vehicles"-->fm;
    graphql--"GET /employees/:id"-->hrdb;
    graphql--"GET /driving-fines"-->pr;
```
## structure

The servcie is structured as a multi module gradle project:

- `graphqltestbench:api` - Graphql schema and exposed POJOs
- `graphqltestbench:service` - the service or domain layer: business logic, dtos to map downstream services responses, etc. Things that can be shared in each of the  controller/application/graphql layer implementations.
- `graphqltestbench:blockingservlet`, `graphqltestbench:asyncservlet`, etc - different implementations of the graphql layer. These modules depend on the others and have main executable classes.

Other directories conain configuration for tools that are useful for testing

- `./downstream/` - a small node app - mocked implementation of the downstream service.
- `./artillery/` - load testing scenarios
- `./elk/` - docker compose config for an elk stack, is set up to consume logs from the service under test and the downsteam mock.
- `./prometheus/` - docker compose config for Prometheus and Grafana. Automatically scrape metrics from the service under test. Can also visualise Artillery's test resuts. 

## setup

### requirements:
- to run the service: a `java 17 jdk` (with some small changes, the code should also compile to java 11)
- to run mocked downstream services & load testing tools: `node` + `npm`
- to run visualisation tools for logs and metrics: `docker` & `docker-compose`
- *(optional)* to run "handy commands": `make`

### tools setup (optional):
1. install Artillery for load testing:   
    ```shell
    npm install -g artillery
    ```
1. launch ELK stack (Kibana on port `5601`):   
    ```shell
    cd ./elk && docker-compose up
    ```
1. launch Prometheus/Grafana (Grafana on port `3001`):   
    ```shell
    cd ./prometheus && docker-compose up
    ```

## launching

1. Launch downstream services: `make run_downstream`
1. Launch one of implementations:

| implementation                                                | command                            | http client                                        | notes                                                                                                                                                                            |
|---------------------------------------------------------------|------------------------------------|----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Blocking Spring MVC servlet with sequential fetcher execution | `make run_blocking_seq`            | Feign (default), blocking                          |                                                                                                                                                                                  |
| Blocking Spring MVC servlet with parallel fetcher execution   | `make run_blocking_par`            | Feign (default), blocking                          |                                                                                                                                                                                  |
| Asyncronous Spring MVC servlet (java11's HTTP Client)         | `make run_async_java11`            | HttpClient (java11), nonblocking                   | no tracing instrumentation                                                                                                                                                       |
| Asyncronous Spring MVC servlet with blocking HTTP clients     | `make run_async_apache_hc_client4` | HttpAsyncClient (Apache HC 4), blocking, but async |                                                                                                                                                                                  |
| Asyncronous Spring MVC servlet (Spring's WebClient)           | `make run_async_webclient`         | WebClient, nonblocking                             |                                                                                                                                                                                  |
| Spring Webflux                                                | `make run_webflux`                 | WebClient, nonblocking                             | graphql-java engine uses CompletableFutures internally and some translations to Mono/Flux are required. To avoid reinventing the (well build) wheel, Spring for Graphql is used. |
| Blocking Spring MVC REST service                              | `make run_pors`                    | Feign (default), blocking                          | plain, old, restful, thread-per-request spring boot web app that returns (almost) the same data.                                                                                 |
