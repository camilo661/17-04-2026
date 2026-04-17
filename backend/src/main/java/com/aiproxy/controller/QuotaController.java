package com.aiproxy.controller;

import com.aiproxy.dto.QuotaStatus;
import com.aiproxy.dto.UsageHistory;
import com.aiproxy.model.Plan;
import com.aiproxy.service.QuotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    /**
     * GET /api/quota/status?userId=...
     * Returns current quota status: tokens used, remaining, resetDate, plan
     */
    @GetMapping("/status")
    public ResponseEntity<QuotaStatus> getStatus(@RequestParam String userId) {
        return ResponseEntity.ok(quotaService.getQuotaStatus(userId));
    }

    /**
     * GET /api/quota/history?userId=...
     * Returns daily usage for the last 7 days
     */
    @GetMapping("/history")
    public ResponseEntity<UsageHistory> getHistory(@RequestParam String userId) {
        return ResponseEntity.ok(quotaService.getUsageHistory(userId));
    }

    /**
     * POST /api/quota/upgrade
     * Upgrades the user's plan (FREE → PRO, etc.)
     */
    @PostMapping("/upgrade")
    public ResponseEntity<QuotaStatus> upgradePlan(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String planName = body.getOrDefault("plan", "PRO").toUpperCase();

        Plan newPlan;
        try {
            newPlan = Plan.valueOf(planName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        QuotaStatus updated = quotaService.upgradePlan(userId, newPlan);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/quota/plans
     * Returns plan details for all tiers
     */
    @GetMapping("/plans")
    public ResponseEntity<Object> getPlans() {
        return ResponseEntity.ok(java.util.List.of(
                Map.of("name", "FREE",       "requestsPerMinute", 10,              "monthlyTokens", 50_000,      "price", 0),
                Map.of("name", "PRO",        "requestsPerMinute", 60,              "monthlyTokens", 500_000,     "price", 19),
                Map.of("name", "ENTERPRISE", "requestsPerMinute", "Unlimited",     "monthlyTokens", "Unlimited", "price", 99)
        ));
    }
}
