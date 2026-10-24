package com.sb.chatbot.chat.service;

import com.sb.chatbot.chat.dto.Source;
import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.faq.FaqRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final FaqRetriever faqRetriever;

    @Value("${faqbot.prompt.system}")
    private Resource systemPrompt;

    public ChatResult ask(String question) {
        long start = System.currentTimeMillis();

        // 1. 검색
        List<Document> docs = faqRetriever.search(question);
        long retrievalMs = System.currentTimeMillis() - start;

        String context = docs.isEmpty()
                ? "(관련 FAQ 없음)"
                : docs.stream().map(Document::getText).collect(Collectors.joining("\n\n"));

        // 2. 생성
        long genStart = System.currentTimeMillis();
        ChatResponse response = chatClient.prompt()
                .system(s -> s.text(systemPrompt).param("context", context))
                .user(question)
                .call()
                .chatResponse();
        long generationMs = System.currentTimeMillis() - genStart;

        Usage usage = response.getMetadata().getUsage();

        return new ChatResult(
                response.getResult().getOutput().getText(),
                docs.stream().map(Source::from).toList(),
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                retrievalMs,
                generationMs,
                System.currentTimeMillis() - start
        );
    }

    public List<Source> search(String question) {
        return faqRetriever.search(question).stream().map(Source::from).toList();
    }
}
