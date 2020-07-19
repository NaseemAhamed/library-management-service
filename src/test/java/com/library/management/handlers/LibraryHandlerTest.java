package com.library.management.handlers;

import com.library.management.common.BeanValidation;
import com.library.management.model.LibraryBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.enums.PathParam;
import com.library.management.processor.BorrowBookProcessor;
import com.library.management.processor.RetrieveBookProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LibraryHandlerTest {

    @InjectMocks LibraryHandler libraryHandler;
    @Spy BeanValidation beanValidation = new BeanValidation();
    @Mock BorrowBookProcessor borrowBookProcessor;
    @Mock RetrieveBookProcessor retrieveBookProcessor;
    @Mock ServerRequest serverRequest;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRetrieveBooks_ResponseRetrieval() {
        LibraryBookDto libraryBookDto = LibraryBookDto
            .builder()
            .bookName("Harry Potter")
            .id("testId")
            .build();
        when(retrieveBookProcessor
            .getBooks()).thenReturn(Flux.fromIterable(Arrays.asList(libraryBookDto)));

        StepVerifier
            .create(libraryHandler.retrieveBooks(serverRequest))
            .expectNextMatches(serverResponse -> serverResponse
                .statusCode()
                .equals(HttpStatus.OK))
            .verifyComplete();
    }

    @Test
    public void testBorrowBookHandler_ResponseRetrieval() {
        when(borrowBookProcessor.borrowBook(any())).thenReturn(Mono.just("book_id_1"));
        RequestDto requestDto = RequestDto
            .builder()
            .id("bookId1")
            .bookName("book1")
            .build();
        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put(PathParam.USER_ID.getKey(), "user1");

        when(serverRequest
            .pathVariables()).thenReturn(pathVariablesMap);
        when(serverRequest.bodyToMono(RequestDto.class)).thenReturn(Mono.just(requestDto));
        StepVerifier
            .create(libraryHandler.borrowBook(serverRequest))
            .expectNextMatches(serverResponse -> serverResponse
                .statusCode()
                .equals(HttpStatus.OK))
            .verifyComplete();
    }
}
