package com.marvrus.vocabularytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VocabularyTestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(VocabularyTestApplication.class, args);
    }

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(VocabularyTestApplication.class);
    }

}
