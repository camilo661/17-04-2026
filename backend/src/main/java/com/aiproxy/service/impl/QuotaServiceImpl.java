package com.aiproxy.service.impl;

import com.aiproxy.config.UserQuotaRegistry;
import com.aiproxy.dto.QuotaStatus;
import com.aiproxy.dto.UsageHistory;
import com.aiproxy.model.Plan;
import com.aiproxy.model.UserQuota;
import com.aiproxy.service.QuotaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuotaServiceImpl implements QuotaService {

    private final UserQuotaRegistry registry;

    public QuotaServiceImpl(UserQuotaRegistry registry) {
        this.registry = registry;
    }

    @Override
    public UserQuota getOrCreateUserQuota(String userId) {
        return registry.getOrCreate(userId);
    }

    @Override
    public QuotaStatus getQuotaStatus(String userId) {
        UserQuota quota = registry.getOrCreate(userId);
        return buildQuotaStatus(quota);
    }

    @Override
    public UsageHistory getUsageHistory(String userId) {
        UserQuota quota = registry.getOrCreate(userId);

        List<UsageHistory.DailyEntry> entries = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Build a 7-day window filled with actual data or zeros
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int tokens = quota.getUsageHistory().stream()
                    .filter(u -> u.getDate().equals(date))
                    .mapToInt(UserQuota.DailyUsage::getTokensUsed)
                    .sum();
            entries.add(new UsageHistory.DailyEntry(date, tokens));
        }

        return new UsageHistory(userId, entries);
    }

    @Override
    public QuotaStatus upgradePlan(String userId, Plan newPlan) {
        UserQuota quota = registry.getOrCreate(userId);
        quota.setPlan(newPlan);
        return buildQuotaStatus(quota);
    }

    @Override
    public void resetAllRateLimits() {
        registry.getAll().values().forEach(UserQuota::resetMinuteWindow);
    }

    @Override
    public void resetMonthlyQuotas() {
        registry.getAll().values().forEach(UserQuota::resetMonthlyQuota);
    }

    private QuotaStatus buildQuotaStatus(UserQuota quota) {
        QuotaStatus status = new QuotaStatus();
        status.setUserId(quota.getUserId());
        status.setPlan(quota.getPlan().name());
        status.setTokensUsed(quota.getTokensUsed());

        int remaining = quota.getPlan().isUnlimited()
                ? Integer.MAX_VALUE
                : quota.getRemainingTokens();
        status.setTokensRemaining(remaining);

        int limit = quota.getPlan().isUnlimited()
                ? Integer.MAX_VALUE
                : quota.getPlan().getMonthlyTokens();
        status.setMonthlyTokenLimit(limit);
        status.setResetDate(quota.getResetDate());
        status.setRequestsThisMinute(quota.getRequestsThisMinute());

        int rpmLimit = quota.getPlan().isUnlimited()
                ? Integer.MAX_VALUE
                : quota.getPlan().getRequestsPerMinute();
        status.setRequestsPerMinuteLimit(rpmLimit);
        status.setRateLimitReached(
                !quota.getPlan().isUnlimited() &&
                quota.getRequestsThisMinute() >= quota.getPlan().getRequestsPerMinute()
        );
        status.setQuotaExhausted(
                !quota.getPlan().isUnlimited() &&
                quota.getRemainingTokens() <= 0
        );
        return status;
    }
}
