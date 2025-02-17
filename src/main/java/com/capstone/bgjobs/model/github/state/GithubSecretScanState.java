package com.capstone.bgjobs.model.github.state;

public enum GithubSecretScanState {
    OPEN("open"),
    RESOLVED("resolved");

    private final String value;

    GithubSecretScanState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
