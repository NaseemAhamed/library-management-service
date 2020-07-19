package com.library.management.handlers;

import com.library.management.error.ErrorStore;
import com.library.management.error.LibraryResponseStatusException;
import com.library.management.model.enums.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class ContextHandler {

    public static Mono<String> getContextUserId() {
        return Mono
            .subscriberContext()
            .map(context -> context.get(PathParam.USER_ID.getKey()))
            .cast(String.class)
            .filter(StringUtils::isNotEmpty)
            .switchIfEmpty(Mono.error(new LibraryResponseStatusException(HttpStatus.BAD_REQUEST,
                ErrorStore.INVALID_REQUEST.getMessage())));
    }
}
