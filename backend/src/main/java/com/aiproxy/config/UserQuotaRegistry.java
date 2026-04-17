package com.aiproxy.config;

import com.aiproxy.model.Plan;
import com.aiproxy.model.UserQuota;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton registry that holds all user quota state in memory.
 * Uses Spring's @Component singleton scope + ConcurrentHashMap for thread safety.
 * Implements the Singleton pattern to ensure a single source of truth for all quotas.
 */
@Component
public class UserQuotaRegistry {

    private static UserQuotaRegistry instance;
    private final ConcurrentHashMap<String, UserQuota> quotaStore = new ConcurrentHashMap<>();

    // Spring manages the singleton lifecycle; this constructor is called once
    public UserQuotaRegistry() {
        instance = this;
        // Seed demo users
        seedDemoUsers();
    }

    public static UserQuotaRegistry getInstance() {
        return instance;
    }

    public UserQuota getOrCreate(String userId) {
        return quotaStore.computeIfAbsent(userId, id -> new UserQuota(id, Plan.FREE));
    }

    public UserQuota get(String userId) {
        return quotaStore.get(userId);
    }

    public void put(String userId, UserQuota quota) {
        quotaStore.put(userId, quota);
    }

    public ConcurrentHashMap<String, UserQuota> getAll() {
        return quotaStore;
    }

    private void seedDemoUsers() {
        quotaStore.put("user-free", new UserQuota("user-free", Plan.FREE));
        quotaStore.put("user-pro", new UserQuota("user-pro", Plan.PRO));
        quotaStore.put("user-enterprise", new UserQuota("user-enterprise", Plan.ENTERPRISE));
    }
}
