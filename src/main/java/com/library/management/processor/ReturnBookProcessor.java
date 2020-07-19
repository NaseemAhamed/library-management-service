package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.error.ErrorStore;
import com.library.management.error.LibraryResponseStatusException;
import com.library.management.handlers.ContextHandler;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.ReturnedBooksDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnBookProcessor {
    private final DbService dbService;
    private static final BiPredicate<RequestDto, BorrowedBookDto> isAValidBorrowedBook = (requestDto, borrowedBookDb) ->
        borrowedBookDb
            .getId()
            .equalsIgnoreCase(requestDto.getId()) && borrowedBookDb
            .getBookName()
            .equalsIgnoreCase(requestDto.getBookName());

    public Mono<String> returnBook(ReturnedBooksDto returnedBooksDto) {
        return
            Flux
                .fromIterable(returnedBooksDto.getBooks())
                .flatMap(this::retrieveBookFromBorrowedList)
                .flatMap(this::registerReturnedBook)
                .flatMap(this::checkoutReturnedBookOrRollBack)
                .collectList()
                .map(bookIds -> String.join(", ", bookIds))
                .filter(StringUtils::isNotEmpty)
                .switchIfEmpty(Mono.defer(() -> Mono.error(bookNotFound(returnedBooksDto
                    .getBooks()
                    .stream()
                    .map(RequestDto::getId)
                    .collect(
                        Collectors.joining(", ")))))
                );

    }

    private Mono<BorrowedBookDto> retrieveBookFromBorrowedList(final RequestDto requestDto) {
        return ContextHandler
            .getContextUserId()
            .flatMap(userId -> dbService
                .findBorrowedListByUser(userId)
                .filter(borrowedBookDb -> isAValidBorrowedBook.test(requestDto, borrowedBookDb))
                .collectList()
                .map(borrowedBookDtoList ->
                    borrowedBookDtoList
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> bookNotFound(requestDto.getId())))
                .switchIfEmpty(
                    Mono.defer(() ->
                        Mono.error(bookNotFound(requestDto.getId())))))
            .doOnError(throwable -> log.error("ISBN "
                .concat(requestDto.getId())
                .concat(StringUtils.SPACE)
                .concat(throwable.getMessage())))
            .onErrorResume(throwable -> Mono.empty());
    }

    private LibraryResponseStatusException bookNotFound(String id) {
        return new LibraryResponseStatusException(HttpStatus.NOT_FOUND,
            ErrorStore.RESOURCE_NOT_FOUND.getMessage(),
            Collections.singletonList(ErrorStore.BOOK_NOT_FOUND_IN_BORROWED_LIST
                .getMessage()
                .concat(StringUtils.SPACE)
                .concat(id)));
    }

    private Mono<String> checkoutReturnedBookOrRollBack(final LibraryBookDto libraryBookDto) {
        return dbService
            .removeBookFromBorrowedList(libraryBookDto.getId())
            .map(value -> libraryBookDto.getId())
            .switchIfEmpty(Mono.just(libraryBookDto.getId()))
            .onErrorResume(throwable -> returnCompensatoryHandler(libraryBookDto));
    }

    private Mono<LibraryBookDto> registerReturnedBook(final BorrowedBookDto borrowBookDto) {

        return dbService
            .saveBookToLibrary(LibraryBookDto
                .builder()
                .id(borrowBookDto.getId())
                .bookName(borrowBookDto.getBookName())
                .authorName(borrowBookDto.getAuthorName())
                .build())
            .doOnNext(libraryBookDto -> log.info("Returned book: {}", libraryBookDto));
    }

    private Mono<@NotNull String> returnCompensatoryHandler(final LibraryBookDto libraryBookDto) {
        return dbService
            .removeBookFromLibrary(libraryBookDto.getId())
            .map(value -> libraryBookDto.getId())
            .switchIfEmpty(
                Mono.defer(() -> Mono.error(
                    new LibraryResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorStore.ERROR_RETURN.getMessage()))));
    }
}
