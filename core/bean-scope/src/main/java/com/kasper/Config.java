package com.kasper;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Collections;

@Configuration
public class Config {

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(Collections.singletonMap("threadScope", new CustomThreadScope()));
        return configurer;
    }

    @Bean
    @Scope("threadScope")
    public ScopedBean scopedBean() {
        return new ScopedBean();
    }
}
