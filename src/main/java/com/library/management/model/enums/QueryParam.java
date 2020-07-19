package com.library.management.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QueryParam {
    ACTION("action");

    @Getter
    private String key;
}
