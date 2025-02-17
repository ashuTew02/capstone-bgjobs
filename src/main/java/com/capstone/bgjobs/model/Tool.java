package com.capstone.bgjobs.model;

public enum Tool {
    CODE_SCAN("code-scan"),
    DEPENDABOT("dependabot"),
    SECRET_SCAN("secret-scan");

    private final String value;

    Tool(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
