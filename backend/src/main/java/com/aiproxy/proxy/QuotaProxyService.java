package com.aiproxy.proxy;

import com.aiproxy.config.UserQuotaRegistry;
import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.exception.QuotaExhaustedException;
import com.aiproxy.model.UserQuota;
import com.aiproxy.service.AIGenerationService;
import com.aiproxy.service.impl.MockAIGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy #2 - Quota Proxy.
 * Intercepts requests after rate limiting and enforces monthly token quotas.
 * Deducts consumed tokens after the real service responds.
 * Returns 402 Payment Required if the monthly quota is exhausted.
 * Part of the Proxy chain: RateLimit → Quota → RealService
 */
public class QuotaProxyService implements AIGenerationService {

    private static final Logger log = LoggerFactory.getLogger(QuotaProxyService.class);

    private final AIGenerationService next;
    private final UserQuotaRegistry registry;

    public QuotaProxyService(AIGenerationService next, UserQuotaRegistry registry) {
        this.next = next;
        this.registry = registry;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserQuota quota = registry.getOrCreate(request.getUserId());

        // ENTERPRISE plan is unlimited — skip quota check
        if (!quota.getPlan().isUnlimited()) {
            if (quota.getRemainingTokens() <= 0) {
                log.warn("Monthly quota exhausted for user: {} (plan: {}, used: {}/{})",
                        request.getUserId(), quota.getPlan(),
                        quota.getTokensUsed(), quota.getPlan().getMonthlyTokens());
                throw new QuotaExhaustedException(
                        "Monthly token quota exhausted. Used " + quota.getTokensUsed() +
                        "/" + quota.getPlan().getMonthlyTokens() + " tokens.",
                        quota.getResetDate()
                );
            }
        }

        // Estimate tokens before the request to check if we have enough
        int estimatedTokens = MockAIGenerationService.estimateTokens(request.getPrompt()) + 300;
        if (!quota.getPlan().isUnlimited() && estimatedTokens > quota.getRemainingTokens()) {
            throw new QuotaExhaustedException(
                    "Insufficient tokens for this request. Estimated: " + estimatedTokens +
                    ", remaining: " + quota.getRemainingTokens(),
                    quota.getResetDate()
            );
        }

        // Delegate to the next in chain (real service)
        GenerationResponse response = next.generate(request);

        // Deduct actual tokens used
        quota.consumeTokens(response.getTokensUsed());
        log.info("User {} consumed {} tokens. Total used: {}/{}",
                request.getUserId(), response.getTokensUsed(),
                quota.getTokensUsed(),
                quota.getPlan().isUnlimited() ? "∞" : quota.getPlan().getMonthlyTokens());

        return response;
    }
}
