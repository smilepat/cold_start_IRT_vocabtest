package com.marvrus.vocabularytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger API Documentation Configuration
 * Access at: http://localhost:8080/swagger-ui/
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.marvrus.vocabularytest.controller.api"))
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("IRT CAT Vocabulary Test API")
                .description("Cold Start IRT-based Computerized Adaptive Testing for Vocabulary Assessment")
                .version("1.0.0")
                .contact(new Contact("Developer", "", ""))
                .build();
    }
}
