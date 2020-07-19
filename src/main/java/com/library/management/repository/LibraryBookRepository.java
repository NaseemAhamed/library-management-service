package com.library.management.repository;

import com.library.management.model.LibraryBookDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LibraryBookRepository extends ReactiveMongoRepository<LibraryBookDto, String> {
    Mono<LibraryBookDto> findByIdAndBookName(String id, String bookName);
}
