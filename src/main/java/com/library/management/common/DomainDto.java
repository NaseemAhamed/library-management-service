package com.library.management.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface DomainDto {
    @JsonIgnore
    default Optional<List<String>> getValidationErrors() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        factory.close();
        return Optional
            .of(validator.validate(this))
            .filter(set -> !set.isEmpty())
            .map(set -> set
                .stream()
                .map(
                    violation -> violation
                        .getPropertyPath()
                        .toString()
                        .concat(StringUtils.SPACE)
                        .concat(violation.getMessage()))
                .collect(Collectors.toList())
            );

    }
}
