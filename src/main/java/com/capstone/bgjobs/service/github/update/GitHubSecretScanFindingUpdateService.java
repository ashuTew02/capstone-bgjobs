package com.capstone.bgjobs.service.github.update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.Tool;
import com.capstone.bgjobs.model.github.dismissedreason.GithubSecretScanDismissedReason;
import com.capstone.bgjobs.model.github.state.GithubSecretScanState;
import com.capstone.bgjobs.service.ElasticSearchService;
import com.capstone.bgjobs.service.mapper.state.GitHubStateMapper;

import reactor.core.publisher.Mono;

@Service
public class GitHubSecretScanFindingUpdateService implements GitHubFindingUpdateService {

    private final WebClient webClient;
    private final GitHubStateMapper<GithubSecretScanState, GithubSecretScanDismissedReason> secretScanMapper;
    private final ElasticSearchService esService;

    public GitHubSecretScanFindingUpdateService(
            WebClient.Builder webClientBuilder,
            GitHubStateMapper<GithubSecretScanState, GithubSecretScanDismissedReason> secretScanMapper,
            ElasticSearchService esService) {

        this.webClient = webClientBuilder
            .baseUrl("https://api.github.com")
            .build();
        this.secretScanMapper = secretScanMapper;
        this.esService = esService;
    }

    @Override
    public Tool getToolType() {
        return Tool.SECRET_SCAN;
    }

    @Override
    public void updateFinding(String owner,
                              String repo,
                              String personalAccessToken,
                              Long alertNumber,
                              FindingState findingState,
                              String esFindingId,
                              Long tenantId) {

        GithubSecretScanState state = secretScanMapper.mapState(findingState);
        Optional<GithubSecretScanDismissedReason> dismissedReasonOpt = secretScanMapper.mapDismissedReason(findingState);

        Map<String, Object> body = new HashMap<>();
        body.put("state", state.getValue());    // "open" or "resolved"
        dismissedReasonOpt.ifPresent(reason -> body.put("resolution", reason.getValue()));

        webClient.patch()
            .uri("/repos/{owner}/{repo}/secret-scanning/alerts/{alertNumber}", owner, repo, alertNumber)
            .header("Authorization", "Bearer " + personalAccessToken)
            .bodyValue(body)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                        Mono.error(new RuntimeException("Failed to update Secret Scanning Alert: " + errorBody))
                    )
            )
            .bodyToMono(Void.class)
            .block();

        esService.updateFindingStateByFindingId(esFindingId, findingState, tenantId);    // will also need tenantID later.
    }
}

