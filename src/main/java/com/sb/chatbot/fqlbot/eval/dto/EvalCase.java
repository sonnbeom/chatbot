package com.sb.chatbot.fqlbot.eval.dto;


import java.util.List;

public record EvalCase(String id,
                       String category,
                       String question,
                       List<String> expectedFaqIds,
                       List<String> mustInclude,
                       List<String> mustNotInclude,
                       String referenceAnswer) {}