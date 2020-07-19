package com.library.management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.common.DomainDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder(toBuilder = true)
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "BOOK_BORROWED")
public class BorrowedBookDto implements DomainDto {
    @Id
    String _id;

    private String bookName;

    private String userId;

    private String authorName;
}
