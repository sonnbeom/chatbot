package com.sb.chatbot.chat.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FaqRetriever {
    private final VectorStore vectorStore;

    @Value("${faqbot.rag.top-k}")
    private int topK;

    @Value("${faqbot.rag.similarity-threshold}")
    private double similarityThreshold;

    public List<Document> search(String question) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(topK) // 비슷한 순서대로 최대 몇개를 가져올지 너무 작으면 정답을 놓치고 너무 크면 관련 없는 내용이 LLM에게 들어감 보통 3~5
                .similarityThreshold(similarityThreshold) //유사도가 이 값(0~1)이상인 것만 가져옴 너무 높으면 아무 것도 안 나오고 너무 낮으면 무관한 답변을 가져옴 보통 0.5에서 0.7부터 시작해서 조정
                .build());
    }
}
