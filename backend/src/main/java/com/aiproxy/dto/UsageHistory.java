package com.aiproxy.dto;

import java.time.LocalDate;
import java.util.List;

public class UsageHistory {

    private String userId;
    private List<DailyEntry> history;

    public UsageHistory() {}

    public UsageHistory(String userId, List<DailyEntry> history) {
        this.userId = userId;
        this.history = history;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<DailyEntry> getHistory() { return history; }
    public void setHistory(List<DailyEntry> history) { this.history = history; }

    public static class DailyEntry {
        private LocalDate date;
        private int tokensUsed;

        public DailyEntry() {}

        public DailyEntry(LocalDate date, int tokensUsed) {
            this.date = date;
            this.tokensUsed = tokensUsed;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public int getTokensUsed() { return tokensUsed; }
        public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    }
}
