package com.capstone.bgjobs.service.mapper.state;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.github.dismissedreason.GithubDependabotDismissedReason;
import com.capstone.bgjobs.model.github.state.GithubDependabotState;

@Service
public class GithubDependabotStateMapper
    implements GitHubStateMapper<GithubDependabotState, GithubDependabotDismissedReason> {

    @Override
    public GithubDependabotState mapState(FindingState state) {
        /*
         * Dependabot has: "open", "dismissed"
         * We'll do:
         *   OPEN -> "open"
         *   FALSE_POSITIVE, SUPPRESSED, FIXED -> "dismissed"
         */
        switch (state) {
            case OPEN:
                return GithubDependabotState.OPEN;
            case FALSE_POSITIVE:
            case SUPPRESSED:
            case FIXED:
            default:
                return GithubDependabotState.DISMISSED;
        }
    }

    @Override
    public Optional<GithubDependabotDismissedReason> mapDismissedReason(FindingState state) {
        /*
         * Dependabot reasons: fix_started, inaccurate, no_bandwidth, not_used, tolerable_risk
         * We'll map:
         *   FALSE_POSITIVE -> "inaccurate"
         *   SUPPRESSED     -> "not_used"
         *   FIXED          -> "fix_started" (some arbitrary choice if you want to represent "fixed")
         */
        switch (state) {
            case FALSE_POSITIVE:
                return Optional.of(GithubDependabotDismissedReason.INACCURATE);
            case SUPPRESSED:
                return Optional.of(GithubDependabotDismissedReason.NOT_USED);
            case FIXED:
                return Optional.of(GithubDependabotDismissedReason.FIX_STARTED);
            default:
                return Optional.empty();
        }
    }
}
