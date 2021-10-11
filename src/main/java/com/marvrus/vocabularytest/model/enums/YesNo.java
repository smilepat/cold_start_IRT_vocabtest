package com.marvrus.vocabularytest.model.enums;

public enum YesNo {
    Y("yes"), N("no");

    YesNo(String description) {
        this.description = description;
    }

    private final String description;

    public String getDescription() {
        return description;
    }
}
