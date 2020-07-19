package com.library.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.common.DomainDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Document(collection = "BOOK_STORE")
public class LibraryBookDto implements DomainDto {
    @Id
    String id;
    private String bookName;
    private String authorName;
}
