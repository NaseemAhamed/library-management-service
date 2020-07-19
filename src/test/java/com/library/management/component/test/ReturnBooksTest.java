package com.library.management.component.test;

import com.library.management.component.Given;
import com.library.management.component.Then;
import com.library.management.component.When;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.RequestDto;
import com.library.management.model.ReturnedBooksDto;
import com.library.management.tags.ComponentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.stream.Stream;

@ComponentTest
@DisplayName("User can return books to the library")
public class ReturnBooksTest {
    @Autowired Given given;
    @Autowired When when;
    @Autowired Then then;

    @Test
    @DisplayName("Test Case ID - 06")
    public void givenTwoBooksInBorrowedList_verifyReturningOneBook() {

        //GIVEN

        RequestDto requestDto = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .userId("user1")
            .build();
        given.clearDbRecords();
        given.initialiseBorrowedListWithBooks();

        //WHEN
        when
            .returnBook("user1", ReturnedBooksDto
                .builder()
                .books(Arrays.asList(requestDto))
                .build())
            .consumeWith(result ->
                {
                    //THEN

                    then.ISeeBookRemovedFromBorrowedList(borrowedBookDto1);
                    then.ISeeBookAddedToLibrary(borrowedBookDto1);

                }
            );
    }

    @Test
    @DisplayName("Test Case ID - 07")
    public void givenTwoBooksInBorrowedList_verifyReturningBothBooks() {

        //GIVEN
        RequestDto requestDto = RequestDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto1 = BorrowedBookDto
            .builder()
            .id("testId1")
            .bookName("bookName1")
            .build();
        BorrowedBookDto borrowedBookDto2 = BorrowedBookDto
            .builder()
            .id("testId2")
            .bookName("bookName2")
            .build();
        given.clearDbRecords();
        given.initialiseBorrowedListWithBooks();

        //WHEN
        when
            .returnBook("user1", ReturnedBooksDto
                .builder()
                .books(Arrays.asList(requestDto))
                .build())
            .consumeWith(result ->
                {
                    //THEN
                    Stream
                        .of(borrowedBookDto1, borrowedBookDto2)
                        .peek(borrowedBookDto -> {
                            then.ISeeBookRemovedFromBorrowedList(borrowedBookDto);
                            then.ISeeBookAddedToLibrary(borrowedBookDto);
                        });
                }
            );
    }
}
