package com.marvrus.vocabularytest.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/environments")
public class EnvironmentApiController {
    @Value("${spring.profiles.active}")
    private String environment;

    @GetMapping("")
    public String getEnvironment() {
        return environment;
    }
}
