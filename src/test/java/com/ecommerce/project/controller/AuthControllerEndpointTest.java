package com.ecommerce.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerEndpointTest {

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot load
        // which indicates compilation/dependency issues
    }
}
