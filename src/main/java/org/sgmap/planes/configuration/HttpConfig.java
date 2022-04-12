package org.sgmap.planes.configuration;

import org.apache.camel.component.http.HttpClientConfigurer;
import org.sgmap.common.utilities.SelfSignedHttpClientConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {

    @Bean(name = "skipSSL")
    public HttpClientConfigurer skipSSL(){
        SelfSignedHttpClientConfigurer config = new SelfSignedHttpClientConfigurer();
        return config;
    }
}
