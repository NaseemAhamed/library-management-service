package com.library.management.routers;

import com.library.management.handlers.LibraryHandler;
import com.library.management.model.enums.ActionType;
import com.library.management.model.enums.QueryParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Version1Router {

    RouterFunction<ServerResponse> doRoute(final LibraryHandler libraryHandler) {
        final String VERSION_PATH = "/v1";
        final String GET_BOOKS = VERSION_PATH.concat("/books");
        final String BORROW_RETURN_BOOKS = GET_BOOKS.concat("/{user_id}");

        return route(
            GET(GET_BOOKS)
            , libraryHandler::retrieveBooks)
            .andRoute(POST(BORROW_RETURN_BOOKS)
                .and(RequestPredicates.queryParam(QueryParam.ACTION.getKey(), ActionType.BORROW.getKey()))
                .and(accept(MediaType.APPLICATION_STREAM_JSON)
                    .and(contentType(MediaType.APPLICATION_STREAM_JSON))), libraryHandler::borrowBook)
            .andRoute(POST(BORROW_RETURN_BOOKS)
                .and(RequestPredicates.queryParam(QueryParam.ACTION.getKey(), ActionType.RETURN.getKey()))
                .and(accept(MediaType.APPLICATION_STREAM_JSON)
                    .and(contentType(MediaType.APPLICATION_STREAM_JSON))), libraryHandler::returnBook)
            ;

    }
}
