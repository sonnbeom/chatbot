package com.sb.chatbot.chat.service;

import com.sb.chatbot.chat.dto.request.ChatRequest;
import com.sb.chatbot.chat.dto.response.ChatResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    // Spring AI에서 제공하는 LLM. 호출용 클라이언트
    // API 형태로 호출하며 프롬프트 구성, 호출, 응답 파싱 등을 담당
    // 자동 구성은 ChatClient.Builder만 빈으로 등록하므로 빌더로 생성
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ChatResult ask(ChatRequest request) {
        long start = System.currentTimeMillis();

        ChatResponse response = chatClient.prompt()
                .user(request.question()) //user = 사용자 메시지를 설정
                .call() // 동기 호출로 LLM에게 보냄 stream은 .stream()을 사용 반환 타입은 callResponseSpec
                .chatResponse();
        long elapseMs = System.currentTimeMillis() - start;
        Usage usage = response.getMetadata().getUsage();

       return ChatResult.builder()
            .answer(response.getResult().getOutput().getText())
            .inputTokens(usage.getPromptTokens())
            .outputTokens(usage.getCompletionTokens())
            .elapsedMs(elapseMs)
            .build();
    }
}
