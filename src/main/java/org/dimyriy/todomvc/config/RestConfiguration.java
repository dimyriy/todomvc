package org.dimyriy.todomvc.config;

import org.dimyriy.todomvc.model.Todo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;

@Configuration
public class RestConfiguration extends RepositoryRestMvcConfiguration {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
                config.setBasePath("/api");
                config.setDefaultMediaType(MediaType.APPLICATION_JSON_UTF8);
                config.useHalAsDefaultJsonMediaType(false);
                config.exposeIdsFor(Todo.class);
            }
        };
    }
}