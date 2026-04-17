package com.aiproxy.service;

import com.aiproxy.dto.QuotaStatus;
import com.aiproxy.dto.UsageHistory;
import com.aiproxy.model.Plan;
import com.aiproxy.model.UserQuota;

/**
 * Interface for quota management operations.
 */
public interface QuotaService {

    UserQuota getOrCreateUserQuota(String userId);

    QuotaStatus getQuotaStatus(String userId);

    UsageHistory getUsageHistory(String userId);

    QuotaStatus upgradePlan(String userId, Plan newPlan);

    void resetAllRateLimits();

    void resetMonthlyQuotas();
}
