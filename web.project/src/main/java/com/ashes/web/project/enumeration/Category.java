package com.ashes.web.project.enumeration;

public enum Category {
    CATEGORY_SMALL("CATEGORY_SMALL"),
    CATEGORY_BIG("CATEGORY_BIG"),
    CATEGORY_MEDIUM("CATEGORY_MEDIUM");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getStatus() {
        return this.category;
    }
}
