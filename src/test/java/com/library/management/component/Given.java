package com.library.management.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.management.model.BorrowedBookDto;
import com.library.management.model.LibraryBookDto;
import com.library.management.repository.BorrowedBookRepository;
import com.library.management.repository.LibraryBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class Given {
    @Autowired LibraryBookRepository libraryBookRepository;
    @Autowired BorrowedBookRepository borrowedBookRepository;

    public void noBooksExistsInTheLibrary() {
        libraryBookRepository
            .deleteAll()
            .block();
    }

    public void noBooksExistsInTheBorrowedList() {
        borrowedBookRepository
            .deleteAll()
            .block();
    }

    public void clearDbRecords() {
        noBooksExistsInTheLibrary();
        noBooksExistsInTheBorrowedList();
    }

    public static List<LibraryBookDto> libraryBookSupplier(String path) {

        Gson gson = new Gson();

        Type userListType = new TypeToken<ArrayList<LibraryBookDto>>() {
        }.getType();

        return gson.fromJson(getJsonString(path),
            userListType);
    }

    public static List<BorrowedBookDto> borrowedBookSupplier(String path) {

        Gson gson = new Gson();

        Type userListType = new TypeToken<ArrayList<BorrowedBookDto>>() {
        }.getType();

        return gson.fromJson(getJsonString(path),
            userListType);
    }

    public static String getJsonString(String path) {
        String content = null;

        File file = null;
        try {
            file = ResourceUtils.getFile(path);
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public void initialiseLibraryWithBooks() {
        libraryBookRepository
            .deleteAll()
            .thenMany(libraryBookRepository.saveAll(
                libraryBookSupplier("classpath:libraryBooks_master.json")))
            .blockLast();
    }

    public void initialiseBorrowedListWithBooks() {
        borrowedBookRepository
            .deleteAll()
            .thenMany(borrowedBookRepository.saveAll(
                borrowedBookSupplier("classpath:borrowedBooks.json")))
            .blockLast();
    }
}
