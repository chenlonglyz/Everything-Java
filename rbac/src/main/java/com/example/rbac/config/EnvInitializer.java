package com.example.rbac.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class EnvInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        Environment env = ctx.getEnvironment();
        System.out.println(
                "[Initializer] spring.data.redis.host = [" +
                        env.getProperty("spring.data.redis.host") + "]"
        );
    }
}

