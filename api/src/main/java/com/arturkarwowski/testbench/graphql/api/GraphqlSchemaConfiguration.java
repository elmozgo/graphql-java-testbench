package com.arturkarwowski.testbench.graphql.api;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;

@Configuration
public class GraphqlSchemaConfiguration {

    @Bean
    public String schemaSdl() throws IOException {
        return Resources.toString(getSchemaUrl(), Charsets.UTF_8);
    }
    @Bean
    public Resource schemaResource() throws IOException {
        return new UrlResource(getSchemaUrl());
    }

    private static URL getSchemaUrl() {
        return Resources.getResource("graphql/schema.graphqls");
    }
}
