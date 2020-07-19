package com.library.management.repository;

import com.library.management.model.BorrowedBookDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BorrowedBookRepository extends ReactiveMongoRepository<BorrowedBookDto, String> {
    Flux<BorrowedBookDto> findByUserId(String id);
}
