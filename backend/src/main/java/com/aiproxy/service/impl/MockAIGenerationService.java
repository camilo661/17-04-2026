package com.aiproxy.service.impl;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import com.aiproxy.service.AIGenerationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * The real AI service implementation that simulates text generation.
 * Represents services like Claude, ChatGPT, or Kimi.
 * Simulates processing latency with Thread.sleep(1200ms).
 */
@Service("mockAIGenerationService")
public class MockAIGenerationService implements AIGenerationService {

    private static final int PROCESSING_DELAY_MS = 1200;

    private static final List<String> AI_MODELS = List.of(
            "Claude Sonnet (Proxy)",
            "GPT-4o (Proxy)",
            "Kimi K2 (Proxy)"
    );

    private static final List<String> RESPONSE_TEMPLATES = List.of(
            "That's a fascinating question! Based on my analysis, %s involves several key dimensions. " +
            "First, we need to consider the underlying principles that govern this domain. " +
            "The interplay between complexity and simplicity often reveals elegant solutions. " +
            "In practical terms, this means approaching the problem systematically while remaining " +
            "open to creative interpretations. The evidence suggests that a multifaceted approach " +
            "yields the most robust outcomes in scenarios like this one.",

            "Excellent prompt! Let me break this down for you. When we examine '%s' carefully, " +
            "we discover layers of meaning that aren't immediately apparent. " +
            "The historical context provides a foundation, while modern interpretations " +
            "challenge our assumptions. Research in this area consistently shows that " +
            "the most effective strategies combine theoretical understanding with practical application. " +
            "I recommend exploring multiple perspectives before settling on a conclusion.",

            "Great question about '%s'! This topic sits at the intersection of multiple disciplines. " +
            "From a technical standpoint, the architecture involves distributed systems that must " +
            "balance performance with reliability. The trade-offs are non-trivial: optimizing for " +
            "one dimension often comes at the expense of another. Industry best practices suggest " +
            "a pragmatic approach that prioritizes the most critical requirements while maintaining " +
            "flexibility for future evolution.",

            "I've processed your request about '%s'. The answer unfolds across several dimensions. " +
            "Cognitively, humans tend to approach this through pattern recognition and analogical reasoning. " +
            "Computationally, the problem space is vast but tractable with the right abstractions. " +
            "The synthesis of these perspectives reveals an elegant solution path that leverages " +
            "both intuition and rigorous analysis. My recommendation is to start with the fundamentals " +
            "and build complexity incrementally.",

            "Analyzing your query about '%s'... This is a rich area with significant depth. " +
            "The primary considerations are accuracy, efficiency, and interpretability. " +
            "State-of-the-art approaches in this domain leverage transformer architectures " +
            "with attention mechanisms that capture long-range dependencies. " +
            "For practical implementation, I suggest starting with a proof of concept, " +
            "measuring the key metrics, and iterating based on empirical feedback. " +
            "The theoretical underpinnings are solid and well-established in the literature."
    );

    private final Random random = new Random();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // Simulate AI processing latency
            Thread.sleep(PROCESSING_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String model = AI_MODELS.get(random.nextInt(AI_MODELS.size()));
        String template = RESPONSE_TEMPLATES.get(random.nextInt(RESPONSE_TEMPLATES.size()));
        String truncatedPrompt = request.getPrompt().length() > 50
                ? request.getPrompt().substring(0, 50) + "..."
                : request.getPrompt();
        String generatedText = String.format(template, truncatedPrompt);

        int tokensUsed = estimateTokens(request.getPrompt()) + estimateTokens(generatedText);
        long processingTime = System.currentTimeMillis() - startTime;

        return new GenerationResponse(generatedText, tokensUsed, model, processingTime, null);
    }

    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        // Approximate: 1 token ≈ 4 characters
        return Math.max(1, text.length() / 4);
    }
}
