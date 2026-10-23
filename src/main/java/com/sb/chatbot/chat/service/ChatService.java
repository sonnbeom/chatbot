package com.sb.chatbot.chat.service;

import com.sb.chatbot.chat.dto.request.ChatRequest;
import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.faq.FaqLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


@Service
@RequiredArgsConstructor
public class ChatService {

    // Spring AI에서 제공하는 LLM. 호출용 클라이언트
    // API 형태로 호출하며 프롬프트 구성, 호출, 응답 파싱 등을 담당
    // 자동 구성은 ChatClient.Builder만 빈으로 등록하므로 빌더로 생성
    private final ChatClient chatClient;
    private final FaqLoader faqLoader;

    @Value("${faqbot.prompt.system}")
    private Resource systemPrompt;

    public ChatResult ask(ChatRequest request) {
        long start = System.currentTimeMillis();


        ChatResponse response = chatClient.prompt()
                .system(s -> s.text(systemPrompt) //system은 LLM에게 줄 시스템프롬프트(역할, 규칙, 참고 자료 등 "지시문") 등을 설정하는 메솓, USER가 사용자 질문이라면 SYSTEM은 너는 FAQ 챗봇이고 아래 자료로 답해라 같은 행동지침
                        .param("faq", faqLoader.content())) // s.text(systemPrompt): 시스템 프롬프트의 템플릿 본문을 지정 (여기서는 .st 파일 내용) .param("faq", faqLoader.content()): 템플릿 안의 {faq} 자리에 값을 채움
                .user(request.question()) //user = 사용자 메시지를 설정
                .call()  // 동기 호출로 LLM에게 보냄 stream은 .stream()을 사용 반환 타입은 callResponseSpec
                .chatResponse();

        long elapseMs = System.currentTimeMillis() - start;
        Usage usage = response.getMetadata().getUsage(); // Usage는 LLM호출에서 사용한 토큰 수 정보를 담은 인터페이스

       return ChatResult.builder()
            .answer(response.getResult().getOutput().getText())
            .inputTokens(usage.getPromptTokens())
            .outputTokens(usage.getCompletionTokens())
            .elapsedMs(elapseMs)
            .build();
    }
}
