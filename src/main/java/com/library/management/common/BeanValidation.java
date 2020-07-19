package com.library.management.common;

import com.library.management.error.ErrorStore;
import com.library.management.error.LibraryResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class BeanValidation {
    public Mono<DomainDto> dtoValidation(final Mono<? extends DomainDto> validateDTOMono) {
        return validateDTOMono
            .switchIfEmpty(
                Mono.defer(() -> Mono.error(new LibraryResponseStatusException(HttpStatus.PRECONDITION_FAILED,
                    ErrorStore.INVALID_REQUEST.getMessage()
                    , Collections.singletonList(ErrorStore.PAYLOAD_REQUIRED.getMessage())))))
            .map(validateDTO -> {
                validateDTO
                    .getValidationErrors()
                    .ifPresent(values -> {
                        throw new LibraryResponseStatusException(HttpStatus.BAD_REQUEST,
                            ErrorStore.INVALID_REQUEST.getMessage()
                            , values);
                    });
                return validateDTO;
            });
    }

}
