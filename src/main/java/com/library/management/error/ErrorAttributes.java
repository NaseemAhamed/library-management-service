package com.library.management.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This adds additional attribute errors which contains list of error messages.
 */
@Component
@Slf4j
public class ErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
        ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request,
            options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)
                ? options.excluding(
                ErrorAttributeOptions.Include.STACK_TRACE) : options);
        Throwable error = getError(request);

        errorAttributes.put("errorRecords", retrieveErrors(error));
        errorAttributes.put("message", retrieveReason(error));
        return errorAttributes;
    }

    private List<String> retrieveErrors(Throwable throwable) {
        return Optional
            .ofNullable(throwable)
            .filter(error -> error instanceof LibraryResponseStatusException &&
                !CollectionUtils.isEmpty(((LibraryResponseStatusException) error).getErrors()))
            .map(error -> ((LibraryResponseStatusException) error).getErrors())
            .orElse(Collections.emptyList());
    }

    private String retrieveReason(Throwable throwable) {
        return Optional
            .ofNullable(throwable)
            .filter(error -> error instanceof LibraryResponseStatusException &&
                StringUtils
                    .isNotEmpty(((LibraryResponseStatusException) error).getReason()))
            .map(error -> ((LibraryResponseStatusException) error).getReason())
            .orElse(StringUtils.EMPTY);
    }

}
