package com.capstone.bgjobs.model.github.dismissedreason;

public enum GithubDependabotDismissedReason {
    FIX_STARTED("fix_started"),
    INACCURATE("inaccurate"),
    NO_BANDWIDTH("no_bandwidth"),
    NOT_USED("not_used"),
    TOLERABLE_RISK("tolerable_risk");

    private final String value;

    GithubDependabotDismissedReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
