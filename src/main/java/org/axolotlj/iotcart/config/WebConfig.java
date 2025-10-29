package org.axolotlj.iotcart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Agrega mapeos de CORS.
     *
     * @param registry El registro de CORS donde se definen las reglas.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica CORS a todas las rutas bajo /api/
                .allowedOrigins("*") // Permite cualquier origen
                .allowedMethods(
                        "GET", 
                        "POST", 
                        "PUT", 
                        "DELETE", 
                        "OPTIONS"
                )
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}