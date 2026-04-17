package com.aiproxy.proxy;

import com.aiproxy.config.UserQuotaRegistry;
import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.exception.RateLimitExceededException;
import com.aiproxy.model.UserQuota;
import com.aiproxy.service.AIGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Proxy #1 - Rate Limit Proxy.
 * Intercepts requests and enforces per-minute request limits based on the user's plan.
 * Returns 429 Too Many Requests if the limit is exceeded.
 * Part of the Proxy chain: RateLimit → Quota → RealService
 */
public class RateLimitProxyService implements AIGenerationService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitProxyService.class);

    private final AIGenerationService next;
    private final UserQuotaRegistry registry;

    public RateLimitProxyService(AIGenerationService next, UserQuotaRegistry registry) {
        this.next = next;
        this.registry = registry;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserQuota quota = registry.getOrCreate(request.getUserId());

        // Reset window if a minute has passed
        LocalDateTime now = LocalDateTime.now();
        if (quota.getMinuteWindowStart().plusMinutes(1).isBefore(now)) {
            quota.resetMinuteWindow();
            log.debug("Rate limit window reset for user: {}", request.getUserId());
        }

        // ENTERPRISE plan is unlimited — skip rate limit check
        if (!quota.getPlan().isUnlimited()) {
            int limit = quota.getPlan().getRequestsPerMinute();
            if (quota.getRequestsThisMinute() >= limit) {
                long secondsUntilReset = java.time.Duration.between(
                        now, quota.getMinuteWindowStart().plusMinutes(1)
                ).getSeconds();
                log.warn("Rate limit exceeded for user: {} (plan: {}, requests: {}/{})",
                        request.getUserId(), quota.getPlan(), quota.getRequestsThisMinute(), limit);
                throw new RateLimitExceededException(
                        "Rate limit exceeded. You have used " + quota.getRequestsThisMinute() +
                        "/" + limit + " requests this minute.",
                        Math.max(1, secondsUntilReset)
                );
            }
        }

        quota.incrementRequests();
        log.debug("User {} — request {}/{} this minute",
                request.getUserId(),
                quota.getRequestsThisMinute(),
                quota.getPlan().getRequestsPerMinute());

        return next.generate(request);
    }
}
