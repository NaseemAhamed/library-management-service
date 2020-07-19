package com.library.management.db;

import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.repository.BorrowedBookRepository;
import com.library.management.repository.LibraryBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DbService {
    private final LibraryBookRepository libraryBookRepository;
    private final BorrowedBookRepository borrowedBookRepository;

    public Flux<LibraryBookDto> getAllBooksFromLibrary() {
        return libraryBookRepository.findAll();
    }

    public Flux<BorrowedBookDto> findBorrowedListByUser(String id) {
        return borrowedBookRepository.findByUserId(id);
    }

    public Mono<LibraryBookDto> findBookInLibrary(String id, String bookName) {
        return libraryBookRepository.findByIdAndBookName(id, bookName);
    }

    public Mono<BorrowedBookDto> saveBorrowedBook(BorrowedBookDto borrowedBookDto) {
        return borrowedBookRepository.save(borrowedBookDto);
    }

    public Mono<LibraryBookDto> saveBookToLibrary(LibraryBookDto borrowedBookDto) {
        return libraryBookRepository.save(borrowedBookDto);
    }

    public Mono<Void> removeBookFromLibrary(String id) {
        return libraryBookRepository.deleteById(id);
    }

    public Mono<Void> removeBookFromBorrowedList(String id) {
        return borrowedBookRepository.deleteById(id);
    }

}
