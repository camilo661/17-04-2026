package com.aiproxy.dto;

import java.time.LocalDate;
import java.util.List;

public class QuotaStatus {

    private String userId;
    private String plan;
    private int tokensUsed;
    private int tokensRemaining;
    private int monthlyTokenLimit;
    private LocalDate resetDate;
    private int requestsThisMinute;
    private int requestsPerMinuteLimit;
    private boolean rateLimitReached;
    private boolean quotaExhausted;

    public QuotaStatus() {}

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    public int getTokensRemaining() { return tokensRemaining; }
    public void setTokensRemaining(int tokensRemaining) { this.tokensRemaining = tokensRemaining; }
    public int getMonthlyTokenLimit() { return monthlyTokenLimit; }
    public void setMonthlyTokenLimit(int monthlyTokenLimit) { this.monthlyTokenLimit = monthlyTokenLimit; }
    public LocalDate getResetDate() { return resetDate; }
    public void setResetDate(LocalDate resetDate) { this.resetDate = resetDate; }
    public int getRequestsThisMinute() { return requestsThisMinute; }
    public void setRequestsThisMinute(int requestsThisMinute) { this.requestsThisMinute = requestsThisMinute; }
    public int getRequestsPerMinuteLimit() { return requestsPerMinuteLimit; }
    public void setRequestsPerMinuteLimit(int requestsPerMinuteLimit) { this.requestsPerMinuteLimit = requestsPerMinuteLimit; }
    public boolean isRateLimitReached() { return rateLimitReached; }
    public void setRateLimitReached(boolean rateLimitReached) { this.rateLimitReached = rateLimitReached; }
    public boolean isQuotaExhausted() { return quotaExhausted; }
    public void setQuotaExhausted(boolean quotaExhausted) { this.quotaExhausted = quotaExhausted; }
}
