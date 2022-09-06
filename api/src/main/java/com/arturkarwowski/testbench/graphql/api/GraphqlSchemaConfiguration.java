package com.arturkarwowski.testbench.graphql.api;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;

@Configuration
public class GraphqlSchemaConfiguration {

    @Bean
    public String schemaSdl() throws IOException {
        URL url = Resources.getResource("graphql/schema.graphqls");
        return Resources.toString(url, Charsets.UTF_8);
    }
}
