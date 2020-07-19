package com.library.management.processor;

import com.library.management.db.DbService;
import com.library.management.error.ErrorStore;
import com.library.management.error.LibraryResponseStatusException;
import com.library.management.handlers.ContextHandler;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowBookProcessor {
    private final DbService dbService;

    private static final Predicate<List<BorrowedBookDto>> isWithinMaxBorrowedList = borrowedBookDtoList1 ->
        borrowedBookDtoList1.size() < 2;

    private static final BiPredicate<BorrowedBookDto, List<BorrowedBookDto>> isBorrowedCopyUnique = (borrowedBookDto1, borrowedBookDtoList1) -> borrowedBookDtoList1
        .stream()
        .noneMatch(book -> book
            .getBookName()
            .equalsIgnoreCase(borrowedBookDto1.getBookName())
        );

    public Mono<String> borrowBook(final BorrowedBookDto borrowedBookDto) {
        return ContextHandler
            .getContextUserId()
            .flatMap(userId -> this.validateUserEligibility(userId, borrowedBookDto))
            .flatMap(userId -> retrieveBookFromLibrary(borrowedBookDto)
                .flatMap(libraryBookDto -> registerBorrowedBook(userId, libraryBookDto))
                .flatMap(this::checkoutBorrowedBookOrRollback)
            );

    }

    private Mono<String> validateUserEligibility(final String userId, final BorrowedBookDto borrowedBookDto) {
        return dbService
            .findBorrowedListByUser(userId)
            .collectList()
            .flatMap(borrowedBookDtoList ->
                isWithinMaxBorrowedList.test(borrowedBookDtoList) ?
                    validateBorrowedBookCopy(userId, borrowedBookDto, borrowedBookDtoList)
                    : Mono.error(new LibraryResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorStore.INVALID_REQUEST.getMessage(),
                    Collections.singletonList(ErrorStore.BORROW_LIMIT_EXCEEDED.getMessage())))
            );

    }

    private Mono<String> validateBorrowedBookCopy(final String userId, final BorrowedBookDto borrowedBookDto,
        List<BorrowedBookDto> borrowedBookDtoList) {
        return isBorrowedCopyUnique.test(borrowedBookDto, borrowedBookDtoList) ?
            Mono.just(userId) :
            Mono.error(new LibraryResponseStatusException(
                HttpStatus.BAD_REQUEST,
                ErrorStore.INVALID_REQUEST.getMessage(),
                Collections.singletonList(ErrorStore.BORROW_COPY_LIMIT_EXCEEDED.getMessage())));
    }

    private Mono<LibraryBookDto> retrieveBookFromLibrary(final BorrowedBookDto borrowedBookDto) {
        return dbService
            .findBookInLibrary(borrowedBookDto.get_id(), borrowedBookDto.getBookName())
            .switchIfEmpty(Mono.defer(() ->
                Mono.error(new LibraryResponseStatusException(HttpStatus.NOT_FOUND,
                    ErrorStore.RESOURCE_NOT_FOUND.getMessage(),
                    Collections.singletonList(ErrorStore.BOOK_NOT_FOUND_IN_LIBRARY.getMessage())))));
    }

    private Mono<BorrowedBookDto> registerBorrowedBook(final String userId, final LibraryBookDto libraryBookDto) {

        return dbService
            .saveBorrowedBook(BorrowedBookDto
                .builder()
                ._id(libraryBookDto.get_id())
                .bookName(libraryBookDto.getBookName())
                .userId(userId)
                .authorName(libraryBookDto.getAuthorName())
                .build())
            .doOnNext(borrowedBookDto -> log.info("Borrowed book: {}", borrowedBookDto))
            .onErrorResume(
                throwable -> Mono.error(
                    new LibraryResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorStore.ERROR_BORROW.getMessage())));
    }

    private Mono<@NotNull String> checkoutBorrowedBookOrRollback(final BorrowedBookDto borrowedBook) {
        return dbService
            .removeBookFromLibrary(borrowedBook.get_id())
            .map(value -> borrowedBook.get_id())
            .switchIfEmpty(Mono.just(borrowedBook.get_id()))
            .onErrorResume(throwable -> borrowCompensatoryHandler(borrowedBook));
    }

    private Mono<@NotNull String> borrowCompensatoryHandler(final BorrowedBookDto borrowedBookDto) {
        return dbService
            .removeBookFromBorrowedList(borrowedBookDto.get_id())
            .map(value -> borrowedBookDto.get_id())
            .switchIfEmpty(
                Mono.defer(() -> Mono.error(
                    new LibraryResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorStore.ERROR_BORROW.getMessage()))));
    }

}
