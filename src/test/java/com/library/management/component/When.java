package com.library.management.component;

import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.model.ReturnedBooksDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Component
public class When {
    @Autowired WebTestClient webTestClient;

    public WebTestClient.ListBodySpec<LibraryBookDto> viewAllBooks() {
        return webTestClient
            .get()
            .uri("/v1/books")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(LibraryBookDto.class);
    }

    public WebTestClient.ResponseSpec borrowBook(String userId, BorrowedBookDto borrowedBookDto) {
        return webTestClient
            .post()
            .uri("/v1/books/"
                .concat(userId)
                .concat("?&action=borrow"))
            .body(Mono.just(borrowedBookDto), BorrowedBookDto.class)
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .header("Content-Type", "application/stream+json")
            .exchange();
    }

    public WebTestClient.BodyContentSpec returnBook(String userId, ReturnedBooksDto returnedBooksDto) {
        return webTestClient
            .post()
            .uri("/v1/books/"
                .concat(userId)
                .concat("?&action=return"))
            .body(Mono.just(returnedBooksDto), ReturnedBooksDto.class)
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .header("Content-Type", "application/stream+json")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody();
    }
}
