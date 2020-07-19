package com.library.management.model;

import com.library.management.common.DomainDto;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class RequestDto implements DomainDto {

    @NotNull
    String id;

    @NotNull
    @Size(min = 1, max = 250)
    private String bookName;

}
