package com.sb.chatbot.fqlbot.eval.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record RunSummary(Long runId,
                         String note,
                         String chatModel,
                         String embeddingModel,
                         String promptVersion,
                         int topK,
                         double similarityThreshold,
                         double temperature,
                         int totalCases,
                         double keywordPassRate,
                         Double retrievalHitRate,
                         Map<String, Double> keywordPassRateByCategory,
                         long avgGenerationMs,
                         long totalInputTokens,
                         long totalOutputTokens,
                         LocalDateTime startedAt,
                         LocalDateTime finishedAt) {}