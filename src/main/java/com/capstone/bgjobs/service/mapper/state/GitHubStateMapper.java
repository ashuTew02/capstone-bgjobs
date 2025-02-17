package com.capstone.bgjobs.service.mapper.state;

import java.util.Optional;

import com.capstone.bgjobs.model.FindingState;

/**
 * A generic interface for mapping a FindingState to
 * (1) A GitHub-specific state enum
 * (2) An optional GitHub-specific dismissed reason enum
 */
public interface GitHubStateMapper<S, R> {

    /**
     * Maps a FindingState to the GitHub-specific State enum.
     */
    S mapState(FindingState findingState);

    /**
     * Maps a FindingState to the GitHub-specific DismissedReason enum.
     * If the tool doesn't support a reason (e.g. secret scans),
     * return Optional.empty().
     */
    Optional<R> mapDismissedReason(FindingState findingState);
}
