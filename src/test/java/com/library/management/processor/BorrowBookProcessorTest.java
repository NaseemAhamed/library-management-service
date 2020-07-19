package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.error.LibraryResponseStatusException;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BorrowBookProcessorTest {
    @Mock DbService dbService;
    @InjectMocks BorrowBookProcessor borrowBookProcessor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenAUserWithMoreThanTwoBorrowedBooks_verifyException() {
        List<BorrowedBookDto> existingBorrowedBooks = Arrays.asList(BorrowedBookDto
                .builder()
                .bookName("book1")
                .userId("user1")
                ._id("id1")
                .build(),
            BorrowedBookDto
                .builder()
                .bookName("book2")
                .userId("user1")
                ._id("id2")
                .build());

        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            .bookName("book3")
            .userId("user1")
            ._id("id3")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(existingBorrowedBooks));
        StepVerifier
            .create(borrowBookProcessor
                .borrowBook(bookToBeBorrowed)
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
                assertEquals("Request is invalid", ex.getReason());
                assertEquals(Collections.singletonList("Maximum borrow limit exceeded for the user"), ex.getErrors());
            })
            .verify();
    }

    @Test
    public void testBorrowingAnUnavailableBook_verifyException() {
        List<BorrowedBookDto> existingBorrowedBooks = Arrays.asList(BorrowedBookDto
            .builder()
            .bookName("book1")
            .userId("user1")
            ._id("id1")
            .build());

        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            .bookName("book3")
            .userId("user1")
            ._id("id3")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(existingBorrowedBooks));
        when(dbService
            .findBookInLibrary(anyString(), anyString())).thenReturn(Mono.empty());
        StepVerifier
            .create(borrowBookProcessor
                .borrowBook(bookToBeBorrowed)
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
                assertEquals("Resource not found", ex.getReason());
                assertEquals(Collections.singletonList("Book is unavailable in the library"), ex.getErrors());
            })
            .verify();
    }

    @Test
    public void givenAUserWithLessThanTwoBorrowedBooks_borrowingAValidBook() {
        List<BorrowedBookDto> existingBorrowedBooks = Arrays.asList(BorrowedBookDto
            .builder()
            .bookName("book1")
            .userId("user1")
            ._id("id1")
            .build());

        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            .bookName("book3")
            .userId("user1")
            ._id("id3")
            .build();

        LibraryBookDto libraryBookDto = LibraryBookDto
            .builder()
            .bookName("book3")
            ._id("id3")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(existingBorrowedBooks));
        when(dbService
            .findBookInLibrary(anyString(), anyString())).thenReturn(Mono.just(libraryBookDto));
        when(dbService
            .saveBorrowedBook(any())).thenReturn(Mono.just(bookToBeBorrowed));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(Mono.empty());

        StepVerifier
            .create(borrowBookProcessor
                .borrowBook(bookToBeBorrowed)
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectNextMatches(id -> id.equals("id3")
            )
            .verifyComplete();
    }

    @Test
    public void givenFailureOfBookRemovalFromLibrary_verifyRollbackOfBorrowedBook() {
        List<BorrowedBookDto> existingBorrowedBooks = Arrays.asList(BorrowedBookDto
            .builder()
            .bookName("book1")
            .userId("user1")
            ._id("id1")
            .build());

        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            .bookName("book3")
            .userId("user1")
            ._id("id3")
            .build();

        LibraryBookDto libraryBookDto = LibraryBookDto
            .builder()
            .bookName("book3")
            ._id("id3")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(existingBorrowedBooks));
        when(dbService
            .findBookInLibrary(anyString(), anyString())).thenReturn(Mono.just(libraryBookDto));
        when(dbService
            .saveBorrowedBook(any())).thenReturn(Mono.just(bookToBeBorrowed));
        when(dbService
            .removeBookFromLibrary(anyString())).thenReturn(
            Mono.error(new LibraryResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));
        when(dbService
            .removeBookFromBorrowedList(anyString())).thenReturn(Mono.empty());

        StepVerifier
            .create(borrowBookProcessor
                .borrowBook(bookToBeBorrowed)
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
                assertEquals("Error while borrowing book", ex.getReason());
            })
            .verify();
        verify(dbService, times(1)).removeBookFromBorrowedList(anyString());
    }

    @Test
    public void givenFailureOfRegistrationOfBorrowedBook_verifyException() {
        List<BorrowedBookDto> existingBorrowedBooks = Arrays.asList(BorrowedBookDto
            .builder()
            .bookName("book1")
            .userId("user1")
            ._id("id1")
            .build());

        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            .bookName("book3")
            .userId("user1")
            ._id("id3")
            .build();

        LibraryBookDto libraryBookDto = LibraryBookDto
            .builder()
            .bookName("book3")
            ._id("id3")
            .build();

        when(dbService
            .findBorrowedListByUser(anyString())).thenReturn(Flux.fromIterable(existingBorrowedBooks));
        when(dbService
            .findBookInLibrary(anyString(), anyString())).thenReturn(Mono.just(libraryBookDto));
        when(dbService
            .saveBorrowedBook(any())).thenReturn(
            Mono.error(new LibraryResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));

        StepVerifier
            .create(borrowBookProcessor
                .borrowBook(bookToBeBorrowed)
                .subscriberContext(context -> context.put(
                    "user_id", "user1")))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
                assertEquals("Error while borrowing book", ex.getReason());
            })
            .verify();
    }
}
