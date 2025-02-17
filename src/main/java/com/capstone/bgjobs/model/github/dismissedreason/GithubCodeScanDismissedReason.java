package com.capstone.bgjobs.model.github.dismissedreason;

public enum GithubCodeScanDismissedReason {
    FALSE_POSITIVE("false positive"),
    WONT_FIX("won't fix"),
    USED_IN_TESTS("used in tests");

    private final String value;

    GithubCodeScanDismissedReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
