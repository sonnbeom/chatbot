package com.sb.chatbot.fqlbot.eval.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvalRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    // 시험 당시 설정
    private String chatModel;
    private String embeddingModel;
    private String promptVersion;
    private int topK;
    private double similarityThreshold;
    private double temperature;

    // 합계
    private int totalCases;
    private int keywordPassCount;
    private int retrievalCaseCount;
    private int retrievalHitCount;
    private long totalInputTokens;
    private long totalOutputTokens;
    private long avgGenerationMs;

    public EvalRun(String note, String chatModel, String embeddingModel, String promptVersion,
                   int topK, double similarityThreshold, double temperature) {
        this.note = note;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.promptVersion = promptVersion;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
        this.temperature = temperature;
        this.startedAt = LocalDateTime.now();
    }

    public void finish(List<EvalResult> results) {
        this.finishedAt = LocalDateTime.now();
        this.totalCases = results.size();
        this.keywordPassCount = (int) results.stream().filter(EvalResult::isKeywordPass).count();
        this.retrievalCaseCount = (int) results.stream().filter(r -> r.getRetrievalHit() != null).count();
        this.retrievalHitCount = (int) results.stream().filter(r -> Boolean.TRUE.equals(r.getRetrievalHit())).count();
        this.totalInputTokens = results.stream().map(EvalResult::getInputTokens)
                .filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
        this.totalOutputTokens = results.stream().map(EvalResult::getOutputTokens)
                .filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
        this.avgGenerationMs = (long) results.stream().mapToLong(EvalResult::getGenerationMs)
                .average().orElse(0);
    }
}