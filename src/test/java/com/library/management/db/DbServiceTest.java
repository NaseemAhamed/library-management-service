//package com.library.management.db;
//
//import com.library.management.model.BorrowedBookDto;
//import com.library.management.model.LibraryBookDto;
//import com.library.management.repository.BorrowedBookRepository;
//import com.library.management.repository.LibraryBookRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doReturn;
//
//public class DbServiceTest {
//    @InjectMocks DbService dbService;
//    @Mock LibraryBookRepository libraryBookRepository;
//    @Mock BorrowedBookRepository borrowedBookRepository;
//    BorrowedBookDto borrowedBookDto;
//    LibraryBookDto libraryBookDto;
//
//    @BeforeEach
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//        libraryBookDto = LibraryBookDto
//            .builder()
//            ._id("testId")
//            .bookName("testLibraryBookName")
//            .build();
//        borrowedBookDto = BorrowedBookDto
//            .builder()
//            .userId("testuserId")
//            .bookName("testBorrowedBookName")
//            ._id("testId")
//            .build();
//        doReturn(Flux.fromIterable(Arrays.asList(libraryBookDto)))
//            .when(libraryBookRepository)
//            .findAll();
//        doReturn(Flux.fromIterable(Arrays.asList(borrowedBookDto)))
//            .when(borrowedBookRepository)
//            .findByUserId(anyString());
//        doReturn(Mono.just(libraryBookDto))
//            .when(libraryBookRepository)
//            .findById(anyString());
//        doReturn(Mono.empty())
//            .when(libraryBookRepository)
//            .deleteById(anyString());
//        doReturn(Mono.empty())
//            .when(borrowedBookRepository)
//            .deleteById(anyString());
//        doReturn(Mono.just(borrowedBookDto))
//            .when(borrowedBookRepository)
//            .save(any());
//        doReturn(Mono.just(libraryBookDto))
//            .when(libraryBookRepository)
//            .save(any());
//    }
//
//    @Test
//    public void testGetAllBooks() {
//
//        StepVerifier
//            .create(dbService.getAllBooksFromLibrary())
//            .expectNextMatches(libraryBookDto -> libraryBookDto
//                .getBookName()
//                .equals("testLibraryBookName"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testFindBorrowedListByUser() {
//        StepVerifier
//            .create(dbService.findBorrowedListByUser("testId"))
//            .expectNextMatches(borrowedBookDto -> borrowedBookDto
//                .getBookName()
//                .equals("testBorrowedBookName"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testFindBook() {
//        StepVerifier
//            .create(dbService.findBookInLibrary("testId"))
//            .expectNextMatches(borrowedBookDto -> borrowedBookDto
//                .getBookName()
//                .equals("testLibraryBookName"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testRemoveBookFromLibrary() {
//        StepVerifier
//            .create(dbService.removeBookFromLibrary("testId"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testRemoveBookFromBorrowedList() {
//        StepVerifier
//            .create(dbService.removeBookFromBorrowedList("testId"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testRegisterBorrowedBook() {
//        StepVerifier
//            .create(dbService.saveBorrowedBook(borrowedBookDto))
//            .expectNextMatches(borrowedBook -> borrowedBook
//                .getBookName()
//                .equals("testBorrowedBookName"))
//            .verifyComplete();
//    }
//
//    @Test
//    public void testSaveBookToLibrary() {
//        StepVerifier
//            .create(dbService.saveBookToLibrary(libraryBookDto))
//            .expectNextMatches(libraryBookDto -> libraryBookDto
//                .getBookName()
//                .equalsIgnoreCase("testLibraryBookName")
//                && libraryBookDto
//                .get_id()
//                .equals("testId")
//            )
//            .verifyComplete();
//
//        ;
//    }
//}
