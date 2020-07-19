package com.library.management.component;

import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.repository.BorrowedBookRepository;
import com.library.management.repository.LibraryBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.util.CollectionUtils;
import reactor.test.StepVerifier;

import java.util.List;

import static com.library.management.component.Given.libraryBookSupplier;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class Then {
    @Autowired BorrowedBookRepository borrowedBookRepository;
    @Autowired LibraryBookRepository libraryBookRepository;

    public static void ISeeEmptyLibrary(EntityExchangeResult<List<LibraryBookDto>> actual) {

        assertTrue(CollectionUtils.isEmpty(actual.getResponseBody()));
    }

    public static void ISeeExpectedRecords(EntityExchangeResult<List<LibraryBookDto>> actual, String expected) {
        assertTrue(
            actual
                .getResponseBody()
                .containsAll(libraryBookSupplier(expected)));
    }

    public void ISeeBookAddedToBorrowedList(BorrowedBookDto borrowedBookDto) {
        StepVerifier
            .create(borrowedBookRepository.existsById(borrowedBookDto.getId()))
            .expectNextMatches(Boolean::booleanValue)
            .verifyComplete();
    }

    public void ISeeBookAddedToLibrary(BorrowedBookDto borrowedBookDto) {
        StepVerifier
            .create(libraryBookRepository.existsById(borrowedBookDto.getId()))
            .expectNextMatches(Boolean::booleanValue)
            .verifyComplete();
    }

    public void ISeeBookRemovedFromLibrary(BorrowedBookDto borrowedBookDto) {
        StepVerifier
            .create(libraryBookRepository.existsById(borrowedBookDto.getId()))
            .expectNextMatches(val -> !val.booleanValue())
            .verifyComplete();
    }

    public void ISeeBookRemovedFromBorrowedList(BorrowedBookDto borrowedBookDto) {
        StepVerifier
            .create(borrowedBookRepository.existsById(borrowedBookDto.getId()))
            .expectNextMatches(val -> !val.booleanValue())
            .verifyComplete();
    }

}
