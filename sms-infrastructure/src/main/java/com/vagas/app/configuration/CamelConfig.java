package com.vagas.app.configuration;

import org.apache.camel.processor.interceptor.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CamelConfig {
    @Value("${logging.trace.enabled}")
    private Boolean tracingEnabled;

    @Bean
    public Tracer camelTracer() {
        Tracer tracer = new Tracer();
        tracer.setTraceExceptions(false);
        tracer.setTraceInterceptors(true);
        tracer.setLogName("com.vagas");
        return tracer;
    }
}
