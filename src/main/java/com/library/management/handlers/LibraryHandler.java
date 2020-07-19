package com.library.management.handlers;

import com.library.management.common.BeanValidation;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.ReturnedBooksDto;
import com.library.management.model.enums.PathParam;
import com.library.management.processor.BorrowBookProcessor;
import com.library.management.processor.RetrieveBookProcessor;
import com.library.management.processor.ReturnBookProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryHandler {
    private final BeanValidation beanValidation;
    private final RetrieveBookProcessor retrieveBookProcessor;
    private final BorrowBookProcessor borrowBookProcessor;
    private final ReturnBookProcessor returnBookProcessor;

    public Context contextInitializer(final Context context, final ServerRequest serverRequest) {
        return context
            .putNonNull(PathParam.USER_ID.getKey(),
                Optional
                    .ofNullable(serverRequest
                        .pathVariables()
                        .get(PathParam.USER_ID.getKey()))
                    .orElse(StringUtils.EMPTY));
    }

    public Mono<ServerResponse> retrieveBooks(final ServerRequest serverRequest) {
        return retrieveBookProcessor
            .getBooks()
            .collectList()
            .flatMap(libraryBookList -> ServerResponse
                .ok()
                .body(Mono.just(libraryBookList), LibraryBookDto.class))
            .doOnError(throwable ->
                log.error(throwable.getMessage()));
    }

    public Mono<ServerResponse> borrowBook(final ServerRequest serverRequest) {
        return
            Mono
                .just(serverRequest)
                .flatMap(request -> request.bodyToMono(RequestDto.class))
                .transform(beanValidation::dtoValidation)
                .cast(RequestDto.class)
                .map(this::buildBorrowedBookDto)
                .flatMap(borrowBookProcessor::borrowBook)
                .flatMap(borrowedBookId -> ServerResponse
                    .ok()
                    .body(Mono.just("Book with Id "
                        .concat(borrowedBookId)
                        .concat(" is borrowed.")), String.class))
                .subscriberContext(context -> contextInitializer(context, serverRequest))
                .doOnError(throwable ->
                    log.error(throwable.getMessage()));
    }

    private BorrowedBookDto buildBorrowedBookDto(final RequestDto requestDto) {
        return BorrowedBookDto
            .builder()
            .id(requestDto.getId())
            .bookName(requestDto.getBookName())
            .build();
    }

    public Mono<ServerResponse> returnBook(final ServerRequest serverRequest) {
        return
            Mono
                .just(serverRequest)
                .flatMap(request -> request.bodyToMono(ReturnedBooksDto.class))
                .transform(beanValidation::dtoValidation)
                .cast(ReturnedBooksDto.class)
                .flatMap(returnBookProcessor::returnBook)
                .flatMap(returnedBookIds -> ServerResponse
                    .ok()
                    .body(Mono.just("Book(s) with the following ID are returned: "
                        .concat(returnedBookIds)
                    ), String.class))
                .subscriberContext(context -> contextInitializer(context, serverRequest))
                .doOnError(throwable ->
                    log.error(throwable.getMessage()));
    }
}
