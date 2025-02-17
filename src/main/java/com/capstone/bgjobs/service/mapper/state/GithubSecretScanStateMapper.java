package com.capstone.bgjobs.service.mapper.state;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.github.dismissedreason.GithubSecretScanDismissedReason;
import com.capstone.bgjobs.model.github.state.GithubSecretScanState;

@Service
public class GithubSecretScanStateMapper
    implements GitHubStateMapper<GithubSecretScanState, GithubSecretScanDismissedReason> {

    @Override
    public GithubSecretScanState mapState(FindingState state) {
        /*
         * Secret scanning has: "open" or "resolved"
         * We'll treat domain FALSE_POSITIVE, SUPPRESSED, FIXED => "resolved"
         */
        switch (state) {
            case OPEN:
                return GithubSecretScanState.OPEN;
            case FALSE_POSITIVE:
            case SUPPRESSED:
            case FIXED:
            default:
                return GithubSecretScanState.RESOLVED;
        }
    }

    @Override
    public Optional<GithubSecretScanDismissedReason> mapDismissedReason(FindingState state) {
        /*
         * GH secret scanning reasons: false_positive, wont_fix, revoked, used_in_tests
         * We'll do:
         *   FALSE_POSITIVE -> "false_positive"
         *   SUPPRESSED     -> "wont_fix"
         *   FIXED          -> "revoked" (represents a final fix)
         */
        switch (state) {
            case FALSE_POSITIVE:
                return Optional.of(GithubSecretScanDismissedReason.FALSE_POSITIVE);
            case SUPPRESSED:
                return Optional.of(GithubSecretScanDismissedReason.WONT_FIX);
            case FIXED:
                return Optional.of(GithubSecretScanDismissedReason.REVOKED);
            default:
                // domain = OPEN => no reason
                return Optional.empty();
        }
    }
}
