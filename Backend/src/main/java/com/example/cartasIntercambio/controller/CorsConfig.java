package com.example.cartasIntercambio.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")                
                .allowedOrigins("http://localhost:9090") // sacar esto en producción, en teoría al tener el mismo dominio en el server no es necesario
                .allowedMethods("*")
                .allowCredentials(true);
        // Configuración del CORS que no permitía mandar queries
    }
}
