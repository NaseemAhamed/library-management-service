package com.library.management.common;

import com.library.management.error.LibraryResponseStatusException;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.ReturnedBooksDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanValidationTest {

    @Test
    public void testBorrowedBook_ForMandatoryFieldValidations() {
        StepVerifier
            .create(new BeanValidation()
                .dtoValidation(Mono.just(RequestDto
                    .builder()
                    .build())))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
                assertEquals("Request is invalid", ex.getReason());
                assertTrue(ex
                    .getErrors()
                    .containsAll(Arrays.asList("_id must not be null", "bookName must not be null")));
            })
            .verify();
    }

    @Test
    public void testReturnedBook_ForMandatoryFieldValidations() {
        StepVerifier
            .create(new BeanValidation()
                .dtoValidation(Mono.just(ReturnedBooksDto
                    .builder()
                    .build())))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
                assertEquals("Request is invalid", ex.getReason());
                assertTrue(ex
                    .getErrors()
                    .containsAll(Arrays.asList("books must not be null")));
            })
            .verify();
    }

    @Test
    public void testWithEmptyRequestBody() {
        StepVerifier
            .create(new BeanValidation()
                .dtoValidation(Mono.empty()))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.PRECONDITION_FAILED, ex.getStatus());
                assertEquals("Request is invalid", ex.getReason());
                assertTrue(ex
                    .getErrors()
                    .containsAll(Arrays.asList("Payload is required")));
            })
            .verify();
    }

    @Test
    public void testRequestDto_ForSizeValidations() {
        StepVerifier
            .create(new BeanValidation()
                .dtoValidation(Mono.just(RequestDto
                    .builder()
                    .bookName("")
                    .build())))
            .expectErrorSatisfies(throwable -> {
                assertTrue(throwable instanceof LibraryResponseStatusException);
                LibraryResponseStatusException ex = (LibraryResponseStatusException) throwable;
                assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
                assertEquals("Request is invalid", ex.getReason());
                assertTrue(ex
                    .getErrors()
                    .contains("bookName size must be between 1 and 250"));
            })
            .verify();
    }

    @Test
    public void testRequestAcceptance() {
        StepVerifier
            .create(new BeanValidation()
                .dtoValidation(Mono.just(BorrowedBookDto
                    .builder()
                    ._id("testId")
                    .bookName(RandomStringUtils.randomAlphabetic(250))
                    .authorName(RandomStringUtils.randomAlphabetic(250))
                    .userId(RandomStringUtils.randomAlphabetic(30))
                    .build())))
            .expectNextCount(1)
            .verifyComplete();
    }
}
