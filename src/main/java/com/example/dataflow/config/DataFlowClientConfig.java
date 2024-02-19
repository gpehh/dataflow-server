package com.example.dataflow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.dataflow.rest.client.DataFlowOperations;
import org.springframework.cloud.dataflow.rest.client.DataFlowTemplate;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class DataFlowClientConfig {


    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = DataFlowTemplate.getDefaultDataflowRestTemplate();
        return restTemplate;
    }
    @Bean
    @Lazy
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON,proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataFlowOperations dataFlowOperations(){
        return new DataFlowTemplate(URI.create("http://localhost:9393"), restTemplate(),new ObjectMapper());
    }
}
