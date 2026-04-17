package com.aiproxy.scheduler;

import com.aiproxy.service.QuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Scheduled tasks for automatic quota and rate-limit resets.
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final QuotaService quotaService;

    public ScheduledTasks(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    /**
     * Reset rate-limit counters every minute for all users.
     * Runs at the start of every minute: 0 * * * * *
     */
    @Scheduled(cron = "0 * * * * *")
    public void resetRateLimits() {
        quotaService.resetAllRateLimits();
        log.debug("Rate-limit windows reset for all users.");
    }

    /**
     * Reset monthly token quotas on the 1st of every month at midnight.
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        quotaService.resetMonthlyQuotas();
        log.info("Monthly token quotas reset. Date: {}", LocalDate.now());
    }
}
