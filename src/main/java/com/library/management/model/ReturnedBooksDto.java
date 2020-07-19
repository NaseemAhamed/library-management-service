package com.library.management.model;

import com.library.management.common.DomainDto;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Getter
public class ReturnedBooksDto implements DomainDto {

    @NotNull
    @Size(min = 1, max = 2)
    List<@Valid RequestDto> books;
}
