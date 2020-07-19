package com.library.management.error;

public enum ErrorStore {

    BOOK_NOT_FOUND_IN_LIBRARY("Book is unavailable in the library"),
    BOOK_NOT_FOUND_IN_BORROWED_LIST("Book is unavailable in the borrowed list of the user for the ISBN"),
    BORROW_LIMIT_EXCEEDED("Maximum borrow limit exceeded for the user"),
    BORROW_COPY_LIMIT_EXCEEDED("A copy of the book is already borrowed by the user"),
    ERROR_BORROW("Error while borrowing book"),
    ERROR_RETURN("Error while returning book"),
    INVALID_REQUEST("Request is invalid"),
    RESOURCE_NOT_FOUND("Resource not found"),
    PAYLOAD_REQUIRED("Payload is required");

    private String message;

    ErrorStore(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
