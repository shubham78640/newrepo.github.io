package com.pinch.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pinch.core.transformation.client.api.InternalDataControllerApi;
import com.pinch.core.transformation.client.cache.TransformationCache;
import com.pinch.core.base.http.PinchRestClient;

@Configuration
public class ClientConfig {

    @Value("${service.transformationmaster.url}")
    private String transformationUrl;

    @Bean
    public InternalDataControllerApi internalDataControllerApi() {
        return new InternalDataControllerApi(new PinchRestClient(transformationUrl));
    }

    @Bean
    public TransformationCache transformationCache(InternalDataControllerApi internalDataControllerApi) {
        return new TransformationCache(internalDataControllerApi);
    }
}
