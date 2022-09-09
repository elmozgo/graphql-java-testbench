# graphql-java test bench

<table>
    <tr>
        <td>
            The goal of this project is to test, play and make experiments with various ways of using the <strong>graphql-java</strong> engine in a <strong>Spring Boot</strong> app.<br/><br/>
            The simulated role of the service under test is a "domain aggregator", "frontend backend" or "gateway" - a service that calls downstream apis and processes their response in order to prepare and serve a unified model for an upstream website or report render.
        </td>
        <td width="30%">
            <img src="f16_engine_pw_f100.jpg" alt="PW F100 engine during testing" width="300"/>
        </td>
    </tr>
</table>


## testing tools
 - **Artillery** is used for load testing
 - **ELK stack** is used as a log server
 - **Prometheus** & **Grafana** are used to store and visualise metrics and test results




| implementation  | http client                                | traceability       |
| --------------- | ------------------------------------------ | ------------------ |
| blockingservlet | Feign                                      | ✔   Spring Sleuth |
| asyncservlet    | HttpClient (java11)                        |                    |
| asyncservlet    | HttpAsyncClient (Apache HTTP Components 4) | ✔  Spring Sleuth  |

