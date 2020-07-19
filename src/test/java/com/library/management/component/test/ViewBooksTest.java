package com.library.management.component.test;

import com.library.management.component.Given;
import com.library.management.component.Then;
import com.library.management.component.When;
import com.library.management.tags.ComponentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ComponentTest
@DisplayName("User can view books in library")
public class ViewBooksTest {
    @Autowired Given given;
    @Autowired When when;

    @Test
    @DisplayName("Test Case ID - 01")
    public void givenNoBooksExistsInTheLibrary_ThenISeeEmptyLibrary() {
        //GIVEN
        given
            .noBooksExistsInTheLibrary();
        //WHEN
        when
            .viewAllBooks()
            //THEN
            .consumeWith(Then::ISeeEmptyLibrary)
        ;

    }

    @Test
    @DisplayName("Test Case ID - 02")
    public void givenThereAreBooksInTheLibrary_verifyViewingAllBooksInTheLibrary() {
        //GIVEN
        given
            .initialiseLibraryWithBooks();
        //WHEN
        when
            .viewAllBooks()
            .consumeWith(result ->
                //THEN
                Then.ISeeExpectedRecords(result, "classpath:libraryBooks_master.json"));

    }

}
