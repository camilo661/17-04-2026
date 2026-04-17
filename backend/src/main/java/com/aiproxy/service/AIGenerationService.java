package com.aiproxy.service;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;

/**
 * Core interface for the AI generation service.
 * Both the real service and all proxy wrappers implement this interface.
 * This is the foundation of the Proxy pattern chain.
 */
public interface AIGenerationService {

    /**
     * Generates text based on the provided request.
     *
     * @param request the generation request containing prompt and user info
     * @return a GenerationResponse with the generated text and metadata
     */
    GenerationResponse generate(GenerationRequest request);
}
