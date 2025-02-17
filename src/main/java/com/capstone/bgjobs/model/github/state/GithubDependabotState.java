package com.capstone.bgjobs.model.github.state;

public enum GithubDependabotState {
    DISMISSED("dismissed"),
    OPEN("open");

    private final String value;

    GithubDependabotState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
