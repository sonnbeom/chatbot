package com.sb.chatbot.fqlbot.eval.dto;

import com.sb.chatbot.fqlbot.eval.domain.EvalResult;

public record ResultView(String caseId,
                         String category,
                         String question,
                         String answer,
                         String sources,
                         Boolean retrievalHit,
                         boolean keywordPass,
                         String keywordReason,
                         Integer inputTokens,
                         Integer outputTokens,
                         long generationMs) {

    public static ResultView from(EvalResult r) {
        return new ResultView(r.getCaseId(), r.getCategory(), r.getQuestion(), r.getAnswer(),
                r.getSources(), r.getRetrievalHit(), r.isKeywordPass(), r.getKeywordReason(),
                r.getInputTokens(), r.getOutputTokens(), r.getGenerationMs());
    }
}