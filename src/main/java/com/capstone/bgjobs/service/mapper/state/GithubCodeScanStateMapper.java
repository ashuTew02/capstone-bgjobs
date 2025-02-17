package com.capstone.bgjobs.service.mapper.state;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.github.dismissedreason.GithubCodeScanDismissedReason;
import com.capstone.bgjobs.model.github.state.GithubCodeScanState;

@Service
public class GithubCodeScanStateMapper
    implements GitHubStateMapper<GithubCodeScanState, GithubCodeScanDismissedReason> {

    @Override
    public GithubCodeScanState mapState(FindingState state) {
        /*
         * GitHub code scanning has: "open" or "dismissed"
         * We'll map:
         *   OPEN -> "open"
         *   FALSE_POSITIVE, SUPPRESSED, FIXED -> "dismissed"
         */
        switch (state) {
            case OPEN:
                return GithubCodeScanState.OPEN;
            case FALSE_POSITIVE:
            case SUPPRESSED:
            case FIXED:
            default:
                return GithubCodeScanState.DISMISSED;
        }
    }

    @Override
    public Optional<GithubCodeScanDismissedReason> mapDismissedReason(FindingState state) {
        /*
         * If we produce "dismissed", pick the GH reason:
         *   FALSE_POSITIVE -> "false positive"
         *   SUPPRESSED     -> "won't fix"
         *   FIXED          -> "used in tests"
         */
        switch (state) {
            case FALSE_POSITIVE:
                return Optional.of(GithubCodeScanDismissedReason.FALSE_POSITIVE);
            case SUPPRESSED:
                return Optional.of(GithubCodeScanDismissedReason.WONT_FIX);
            case FIXED:
                return Optional.of(GithubCodeScanDismissedReason.USED_IN_TESTS);
            default:
                // If domain = OPEN, no dismissed reason
                return Optional.empty();
        }
    }
}
