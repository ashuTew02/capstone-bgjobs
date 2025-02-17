package com.capstone.bgjobs.model.github.state;

public enum GithubCodeScanState {
    OPEN("open"),
    DISMISSED("dismissed");

    private final String value;

    GithubCodeScanState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
