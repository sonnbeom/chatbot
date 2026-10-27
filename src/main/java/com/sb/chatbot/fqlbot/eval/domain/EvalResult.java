package com.sb.chatbot.fqlbot.eval.domain;

import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.fqlbot.eval.dto.EvalCase;
import com.sb.chatbot.fqlbot.eval.scorer.ScoreResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvalResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    private EvalRun run;

    private String caseId;
    private String category;

    @Column(length = 1000)
    private String question;

    @Column(length = 4000)
    private String answer;

    @Column(length = 1000)
    private String sources;

    private Boolean retrievalHit;
    private boolean keywordPass;

    @Column(length = 1000)
    private String keywordReason;

    private Integer inputTokens;
    private Integer outputTokens;
    private long retrievalMs;
    private long generationMs;

    public EvalResult(EvalRun run, EvalCase evalCase, ChatResult result,
                      Boolean retrievalHit, ScoreResult keyword) {
        this.run = run;
        this.caseId = evalCase.id();
        this.category = evalCase.category();
        this.question = evalCase.question();
        this.answer = result.answer();
        this.sources = result.sources().stream()
                .map(s -> "%s(%.2f)".formatted(s.faqId(), s.score()))
                .collect(Collectors.joining(", "));
        this.retrievalHit = retrievalHit;
        this.keywordPass = keyword.pass();
        this.keywordReason = keyword.reason();
        this.inputTokens = result.inputTokens();
        this.outputTokens = result.outputTokens();
        this.retrievalMs = result.retrievalMs();
        this.generationMs = result.generationMs();
    }
}