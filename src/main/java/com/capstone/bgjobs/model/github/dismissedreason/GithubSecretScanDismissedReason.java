package com.capstone.bgjobs.model.github.dismissedreason;

public enum GithubSecretScanDismissedReason {
    FALSE_POSITIVE("false_positive"),
    WONT_FIX("wont_fix"),
    REVOKED("revoked"),
    USED_IN_TESTS("used_in_tests");

    private final String value;

    GithubSecretScanDismissedReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
