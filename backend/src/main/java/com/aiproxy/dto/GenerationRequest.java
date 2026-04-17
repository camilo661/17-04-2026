package com.aiproxy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenerationRequest {

    @NotBlank(message = "Prompt cannot be blank")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    private String model = "claude-proxy";

    public GenerationRequest() {}

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
