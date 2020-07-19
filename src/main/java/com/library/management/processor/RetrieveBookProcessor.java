package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.model.LibraryBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class RetrieveBookProcessor {
    private final DbService dbService;

    public Flux<LibraryBookDto> getBooks() {
        return dbService
            .getAllBooksFromLibrary();
    }
}
