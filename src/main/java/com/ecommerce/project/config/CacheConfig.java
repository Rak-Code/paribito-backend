package com.ecommerce.project.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Simple in-memory cache manager. Replace with RedisCacheManager / CaffeineCacheManager for production.
        return new ConcurrentMapCacheManager(
                "product",
                "productsAll",
                "productsPage",
                "productsByCategory",
                "productsBySearch"
        );
    }
}
