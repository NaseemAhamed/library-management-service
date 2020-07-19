package com.library.management.component.test;

import com.library.management.component.Given;
import com.library.management.component.Then;
import com.library.management.component.When;
import com.library.management.model.BorrowedBookDto;
import com.library.management.tags.ComponentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ComponentTest
@DisplayName("User can borrow a book from the library")
public class BorrowBooksTest {
    @Autowired Given given;
    @Autowired When when;
    @Autowired Then then;

    @Test
    @DisplayName("Test Case ID - 03")
    public void givenThereAreBooksInTheLibrary_verifyBorrowingCoyBookFromTheLibrary() {
        //GIVEN
        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            ._id("testId1")
            .bookName("bookName1")
            .build();
        given.initialiseLibraryWithBooks();
        given.noBooksExistsInTheBorrowedList();
        //WHEN
        when
            .borrowBook("user1", bookToBeBorrowed)
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(result ->
                {
                    //THEN
                    then.ISeeBookAddedToBorrowedList(bookToBeBorrowed);
                    then.ISeeBookRemovedFromLibrary(bookToBeBorrowed);
                }
            );
    }

    @Test
    @DisplayName("Test Case ID - 03 - Validation Check")
    public void givenThereAreBooksInTheLibrary_verify400WhileBorrowingMoreThan2BooksFromTheLibrary() {
        //GIVEN
        BorrowedBookDto bookToBeBorrowed = BorrowedBookDto
            .builder()
            ._id("testId3")
            .bookName("bookName2")//user1 has borrowed two books in borrowedBooks.json
            .build();
        given.clearDbRecords();
        given.initialiseBorrowedListWithBooks();
        given.initialiseLibraryWithBooks();
        //WHEN
        when
            .borrowBook("user1", bookToBeBorrowed)
            //THEN
            .expectStatus()
            .isBadRequest();
    }
}
