package com.sb.chatbot.chat.dto.response;

import com.sb.chatbot.chat.dto.Source;
import lombok.Builder;
import java.util.List;
@Builder
public record ChatResult(String answer,
                         List<Source> sources,
                         Integer inputTokens,
                         Integer outputTokens,
                         long retrievalMs,
                         long generationMs,
                         long elapsedMs) {}