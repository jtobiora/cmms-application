/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.config.cache;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Configuration
@EnableCaching
public class InMemoryCachingConfig {
    /**
     * Caching Configuration
     * @return 
     */
    @Bean
    public CacheManager cacheManager() {
        GuavaCacheManager mgr = new GuavaCacheManager();
        mgr.setCacheBuilder( CacheBuilder.newBuilder().expireAfterAccess(24, TimeUnit.HOURS).maximumSize(1_000_000));
        mgr.setCacheNames(Arrays.asList("mandateStatusesByName"));
        return mgr;
    }
}
