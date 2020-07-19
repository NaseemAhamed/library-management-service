package com.library.management.component.model;

import com.library.management.model.LibraryBookDto;
import lombok.Data;

import java.util.List;

@Data
public class ViewBooksResponse {
    List<LibraryBookDto> libraryBooks;
}
