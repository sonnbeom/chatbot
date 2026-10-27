package com.sb.chatbot.fqlbot.eval.scorer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JudgeVerdict(
        @JsonProperty(required = true) String reason,
        @JsonProperty(required = true) int score) {}