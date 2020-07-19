package com.library.management.routers;

import com.library.management.handlers.LibraryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Router {
    @Bean
    RouterFunction<ServerResponse> mainRouterFunction(final Version1Router version1Router,
        final LibraryHandler libraryHandler) {
        return version1Router.doRoute(libraryHandler);
    }
}
