package com.sb.chatbot.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatResult(String answer,
                         Integer inputTokens,
                         Integer outputTokens,
                         long elapsedMs) {}