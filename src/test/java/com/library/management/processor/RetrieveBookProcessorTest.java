package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.model.LibraryBookDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

public class RetrieveBookProcessorTest {
    @Mock DbService dbService;
    @InjectMocks RetrieveBookProcessor retrieveBookProcessor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBooks() {
        LibraryBookDto libraryBookDto = LibraryBookDto
            .builder()
            .bookName("testBookName")
            .id("testBookId")
            .build();
        when(dbService.getAllBooksFromLibrary()).thenReturn(Flux.just(libraryBookDto));
        StepVerifier
            .create(retrieveBookProcessor.getBooks())
            .expectNextMatches(libraryBookDto1 -> libraryBookDto1
                .getBookName()
                .equals("testBookName")
                && libraryBookDto1
                .getId()
                .equals("testBookId"))
            .verifyComplete();
    }
}
