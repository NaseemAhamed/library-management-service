package com.library.management.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PathParam {
    USER_ID("user_id");

    @Getter
    private String key;
}
