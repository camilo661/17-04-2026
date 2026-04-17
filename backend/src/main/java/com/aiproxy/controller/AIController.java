package com.aiproxy.controller;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.dto.QuotaStatus;
import com.aiproxy.service.AIGenerationService;
import com.aiproxy.service.QuotaService;
import com.aiproxy.service.impl.MockAIGenerationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService proxyChain;
    private final QuotaService quotaService;

    public AIController(
            @Qualifier("aiServiceProxyChain") AIGenerationService proxyChain,
            QuotaService quotaService) {
        this.proxyChain = proxyChain;
        this.quotaService = quotaService;
    }

    /**
     * POST /api/ai/generate
     * Runs the full proxy chain: RateLimit → Quota → MockAI
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate(@Valid @RequestBody GenerationRequest request) {
        GenerationResponse response = proxyChain.generate(request);

        // Attach quota status to response
        QuotaStatus quotaStatus = quotaService.getQuotaStatus(request.getUserId());
        response.setQuotaStatus(quotaStatus);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/ai/estimate?prompt=...
     * Returns estimated token count before sending a request
     */
    @GetMapping("/estimate")
    public ResponseEntity<Map<String, Object>> estimateTokens(@RequestParam String prompt) {
        int estimatedTokens = MockAIGenerationService.estimateTokens(prompt) + 300;
        return ResponseEntity.ok(Map.of(
                "promptLength", prompt.length(),
                "estimatedTokens", estimatedTokens
        ));
    }
}
