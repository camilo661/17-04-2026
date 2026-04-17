package com.aiproxy.dto;

public class GenerationResponse {

    private String generatedText;
    private int tokensUsed;
    private String model;
    private long processingTimeMs;
    private QuotaStatus quotaStatus;

    public GenerationResponse() {}

    public GenerationResponse(String generatedText, int tokensUsed, String model,
                               long processingTimeMs, QuotaStatus quotaStatus) {
        this.generatedText = generatedText;
        this.tokensUsed = tokensUsed;
        this.model = model;
        this.processingTimeMs = processingTimeMs;
        this.quotaStatus = quotaStatus;
    }

    public String getGeneratedText() { return generatedText; }
    public void setGeneratedText(String generatedText) { this.generatedText = generatedText; }
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    public QuotaStatus getQuotaStatus() { return quotaStatus; }
    public void setQuotaStatus(QuotaStatus quotaStatus) { this.quotaStatus = quotaStatus; }
}
