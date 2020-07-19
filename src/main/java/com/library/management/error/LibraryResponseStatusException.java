package com.library.management.error;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.List;

public class LibraryResponseStatusException extends ResponseStatusException implements Serializable {

    private static final long serialVersionUID = 7748649892403078143L;
    private final List<String> errors;

    /**
     * Constructor with a response status.
     *
     * @param status the HTTP status (required)
     */
    public LibraryResponseStatusException(HttpStatus status) {
        this(status, null, null, null);
    }

    /**
     * Constructor with a response status and a reason to add to the exception
     * message as explanation.
     *
     * @param status the HTTP status (required)
     * @param reason the associated reason (optional)
     */
    public LibraryResponseStatusException(HttpStatus status, String reason) {
        this(status, reason, null, null);
    }

    /**
     * Constructor with a response status and a reason to add to the exception
     * message as explanation.
     *
     * @param status the HTTP status (required)
     * @param reason the associated reason (optional)
     * @param errors the associated list of errors (optional)
     */
    public LibraryResponseStatusException(HttpStatus status, String reason, List<String> errors) {
        this(status, reason, errors, null);
    }

    /**
     * Constructor with a response status and a reason to add to the exception
     * message as explanation, as well as a nested exception.
     *
     * @param status the HTTP status (required)
     * @param reason the associated reason (optional)
     * @param errors the associated list of errors (optional)
     * @param cause  a nested exception (optional)
     */
    public LibraryResponseStatusException(HttpStatus status, @Nullable String reason,
        @Nullable List<String> errors, @Nullable Throwable cause) {
        super(status, reason, cause);
        this.errors = errors;
    }

    /**
     * Returns the list of errors associated with this exception.
     */
    public List<String> getErrors() {
        return this.errors;
    }
}
