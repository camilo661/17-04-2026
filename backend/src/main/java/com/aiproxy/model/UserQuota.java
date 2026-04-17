package com.aiproxy.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserQuota {

    private final String userId;
    private Plan plan;
    private int tokensUsed;
    private int requestsThisMinute;
    private LocalDateTime minuteWindowStart;
    private LocalDate resetDate;
    private final List<DailyUsage> usageHistory;

    public UserQuota(String userId, Plan plan) {
        this.userId = userId;
        this.plan = plan;
        this.tokensUsed = 0;
        this.requestsThisMinute = 0;
        this.minuteWindowStart = LocalDateTime.now();
        this.resetDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        this.usageHistory = new ArrayList<>();
    }

    // Getters
    public String getUserId() { return userId; }
    public Plan getPlan() { return plan; }
    public int getTokensUsed() { return tokensUsed; }
    public int getRequestsThisMinute() { return requestsThisMinute; }
    public LocalDateTime getMinuteWindowStart() { return minuteWindowStart; }
    public LocalDate getResetDate() { return resetDate; }
    public List<DailyUsage> getUsageHistory() { return usageHistory; }

    // Setters
    public void setPlan(Plan plan) { this.plan = plan; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    public void setRequestsThisMinute(int requestsThisMinute) { this.requestsThisMinute = requestsThisMinute; }
    public void setMinuteWindowStart(LocalDateTime minuteWindowStart) { this.minuteWindowStart = minuteWindowStart; }
    public void setResetDate(LocalDate resetDate) { this.resetDate = resetDate; }

    public void incrementRequests() {
        this.requestsThisMinute++;
    }

    public void consumeTokens(int tokens) {
        this.tokensUsed += tokens;
        recordDailyUsage(tokens);
    }

    public int getRemainingTokens() {
        if (plan.isUnlimited()) return Integer.MAX_VALUE;
        return Math.max(0, plan.getMonthlyTokens() - tokensUsed);
    }

    public void resetMonthlyQuota() {
        this.tokensUsed = 0;
        this.resetDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
    }

    public void resetMinuteWindow() {
        this.requestsThisMinute = 0;
        this.minuteWindowStart = LocalDateTime.now();
    }

    private void recordDailyUsage(int tokens) {
        LocalDate today = LocalDate.now();
        usageHistory.stream()
                .filter(u -> u.getDate().equals(today))
                .findFirst()
                .ifPresentOrElse(
                        u -> u.addTokens(tokens),
                        () -> usageHistory.add(new DailyUsage(today, tokens))
                );
        // Keep only last 7 days
        if (usageHistory.size() > 7) {
            usageHistory.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            while (usageHistory.size() > 7) {
                usageHistory.remove(usageHistory.size() - 1);
            }
        }
    }

    public static class DailyUsage {
        private final LocalDate date;
        private int tokensUsed;

        public DailyUsage(LocalDate date, int tokensUsed) {
            this.date = date;
            this.tokensUsed = tokensUsed;
        }

        public LocalDate getDate() { return date; }
        public int getTokensUsed() { return tokensUsed; }
        public void addTokens(int tokens) { this.tokensUsed += tokens; }
    }
}
