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
@DisplayName("User can borrow a copy of a book from the library")
public class BorrowCopyBooksTest {
    @Autowired Given given;
    @Autowired When when;
    @Autowired Then then;

    @Test
    @DisplayName("Test Case ID - 04")
    public void givenMoreThanOneCopyInTheLibrary_verifyBorrowingTheCopyFromTheLibrary() {

        //GIVEN
        BorrowedBookDto borrowedBookDto = BorrowedBookDto
            .builder()
            ._id("testId1")
            .bookName("bookName1") //We have another bookName1 in libraryBooks_master.json
            .build();
        given.initialiseLibraryWithBooks();
        given.noBooksExistsInTheBorrowedList();
        //WHEN
        when
            .borrowBook("user1", borrowedBookDto)
            .expectBody()
            .consumeWith(result ->
                {
                    //THEN
                    then.ISeeBookAddedToBorrowedList(borrowedBookDto);
                    then.ISeeBookRemovedFromLibrary(borrowedBookDto);
                }
            );
    }

    @Test
    @DisplayName("Test Case ID - 05")
    public void givenExactlyOneCopyInTheLibrary_verifyBorrowingTheCopyFromTheLibrary() {

        //GIVEN
        BorrowedBookDto borrowedBookDto = BorrowedBookDto
            .builder()
            ._id("testId3")
            .bookName("bookName2") //We have only one copy of bookName2 in libraryBooks_master.json
            .build();
        given.initialiseLibraryWithBooks();
        given.noBooksExistsInTheBorrowedList();
        //WHEN
        when
            .borrowBook("user1", borrowedBookDto)
            .expectBody()
            .consumeWith(result ->
                {
                    //THEN
                    then.ISeeBookAddedToBorrowedList(borrowedBookDto);
                    then.ISeeBookRemovedFromLibrary(borrowedBookDto);
                }
            );
    }

    @Test
    @DisplayName("Test Case ID - 05 - ValidationCheck")
    public void givenMoreThanOneCopyInTheLibrary_verify400WhileBorrowingMoreThan1CopyFromTheLibrary() {

        //GIVEN
        BorrowedBookDto borrowedBookDto = BorrowedBookDto
            .builder()
            ._id("testId6")
            .bookName("bookName4") // bookName4 already exists in borrowedBooks.json
            .build();
        given.clearDbRecords();
        given.initialiseLibraryWithBooks();
        given.initialiseBorrowedListWithBooks();
        //WHEN
        when
            .borrowBook("user1", borrowedBookDto)
            //THEN
            .expectStatus()
            .isBadRequest();
    }
}
