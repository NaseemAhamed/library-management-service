package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.error.LibraryResponseStatusException;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.ReturnedBooksDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Component
@RequiredArgsConstructor
public class ReturnBookProcessorTest {
    @Mock DbService dbService;
    @InjectMocks ReturnBookProcessor returnBookProcessor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenTwoBooksInBorrowedList_verifyReturningABook() {
        RequestDto requestDto = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .userId("userId1")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(
            Collections.singletonList(borrowedBookDto1)));
        when(dbService
            .removeBookFromBorrowedList(anyString())).thenReturn(Mono.empty());
        when(dbService
            .saveBookToLibrary(any())).thenReturn(Mono.just(LibraryBookDto
            .builder()
            .bookName("bookName1")
            .id("testId1")
            .build()));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(Mono.empty());
        StepVerifier
            .create(returnBookProcessor
                .returnBook(ReturnedBooksDto
                    .builder()
                    .books(Collections.singletonList(requestDto))
                    .build())
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectNextMatches(returnedBookId ->
                returnedBookId.equalsIgnoreCase("testId1"))
            .verifyComplete();
    }

    @Test
    public void givenFailureOfBookRemovalFromBorrowedList_verifyRollbackOfReturnedBook() {
        RequestDto requestDto = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .userId("userId1")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(
            Collections.singletonList(borrowedBookDto1)));
        when(dbService
            .removeBookFromBorrowedList(anyString())).thenReturn(Mono.error(new LibraryResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR)));
        when(dbService
            .saveBookToLibrary(any())).thenReturn(Mono.just(LibraryBookDto
            .builder()
            .bookName("bookName1")
            .id("testId1")
            .build()));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(Mono.empty());
        StepVerifier
            .create(returnBookProcessor
                .returnBook(ReturnedBooksDto
                    .builder()
                    .books(Collections.singletonList(requestDto))
                    .build())
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
                assertEquals("Error while returning book", ex.getReason());
            })
            .verify();
        verify(dbService, times(1)).removeBookFromLibrary(anyString());
    }

    @Test
    public void testReturningAnUnavailableBorrowedBook_verifyException() {
        RequestDto requestDto = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .userId("userId1")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(
            Collections.emptyList()));
        when(dbService
            .removeBookFromBorrowedList(anyString())).thenReturn(Mono.empty());
        when(dbService
            .saveBookToLibrary(any())).thenReturn(Mono.just(LibraryBookDto
            .builder()
            .bookName("bookName1")
            .id("testId1")
            .build()));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(Mono.empty());
        StepVerifier
            .create(returnBookProcessor
                .returnBook(ReturnedBooksDto
                    .builder()
                    .books(Collections.singletonList(requestDto))
                    .build())
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
                assertEquals("Resource not found", ex.getReason());
                assertEquals(Collections.singletonList(
                    "Book is unavailable in the borrowed list of the user for the ISBN testId1"),
                    ex.getErrors());
            })
            .verify();
    }

    @Test
    public void testReturningBothUnavailableAndAvailableBorrowedBook() {
        //GIVEN
        RequestDto requestDto1 = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        RequestDto requestDto2 = RequestDto
            .builder()
            .id("testId2")
            .bookName("bookName2")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .userId("userId1")
            .build();

        //WHEN
        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(
            Arrays.asList(borrowedBookDto1)));
        when(dbService
            .removeBookFromBorrowedList(anyString())).thenReturn(Mono.empty());
        when(dbService
            .saveBookToLibrary(any())).thenReturn(Mono.just(LibraryBookDto
            .builder()
            .bookName("bookName1")
            .id("testId1")
            .build()));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(Mono.empty());
        //THEN
        StepVerifier
            .create(returnBookProcessor
                .returnBook(ReturnedBooksDto
                    .builder()
                    .books(Arrays.asList(requestDto1, requestDto2))
                    .build())
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectNextMatches(returnedBookId ->
                returnedBookId.equalsIgnoreCase("testId1"))
            .verifyComplete();
    }
}
