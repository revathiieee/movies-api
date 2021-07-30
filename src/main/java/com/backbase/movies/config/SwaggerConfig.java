package com.backbase.movies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Configuration for Swagger
 *
 * @author Revathi
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Swagger bean configuration
     *
     * @return Docket object
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.backbase.movies.controller"))
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo("Movie API",
                "Documentation for Movie API",
                "1.0",
                "Terms of service for using movie app",
                new Contact("revathik", "http://localhost:8090", "revathieee@gmail.com"),
                "MIT Licence",
                "http://opensource.org/licences/MIT",
                new ArrayList<>()
        );
    }
}
