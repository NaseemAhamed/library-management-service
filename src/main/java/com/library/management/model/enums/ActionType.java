package com.library.management.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {
    BORROW("borrow"),
    RETURN("return");

    @Getter
    private String key;
}
