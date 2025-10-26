package com.proyecto.cakecandy_compra_service.infraestructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientAuthConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Obtenemos la petición HTTP actual que llegó al controlador
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    // Extraemos el encabezado "Authorization" de la petición original
                    String authorizationHeader = attributes.getRequest().getHeader("Authorization");
                    if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                        // Lo añadimos a la petición saliente de Feign
                        template.header("Authorization", authorizationHeader);
                    }
                }
            }
        };
    }
}