package com.marvrus.vocabularytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 캘리브레이션 스케줄링 활성화
public class VocabularyTestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(VocabularyTestApplication.class, args);
    }

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(VocabularyTestApplication.class);
    }

}
