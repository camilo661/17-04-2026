package com.aiproxy.config;

import com.aiproxy.proxy.QuotaProxyService;
import com.aiproxy.proxy.RateLimitProxyService;
import com.aiproxy.service.AIGenerationService;
import com.aiproxy.service.impl.MockAIGenerationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Assembles the Proxy chain in the correct order:
 *   RateLimitProxy → QuotaProxy → MockAIGenerationService (real)
 *
 * Uses the Decorator variant of the Proxy pattern — each wrapper
 * calls the next in chain, enabling clean separation of concerns.
 */
@Configuration
public class ProxyChainFactory {

    @Bean(name = "aiServiceProxyChain")
    public AIGenerationService buildProxyChain(
            MockAIGenerationService realService,
            UserQuotaRegistry registry) {

        // Layer 2: Quota check (wraps the real service)
        AIGenerationService quotaProxy = new QuotaProxyService(realService, registry);

        // Layer 1: Rate limit check (outermost layer — checked first)
        return new RateLimitProxyService(quotaProxy, registry);
    }
}
