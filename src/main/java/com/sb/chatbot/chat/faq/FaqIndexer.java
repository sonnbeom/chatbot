package com.sb.chatbot.chat.faq;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

//앱이 작동한 뒤 FAQ를 임베딩해서 저장합니다.
@Slf4j
@Component
@RequiredArgsConstructor
public class FaqIndexer {

    private final FaqLoader faqLoader;
    private final FaqParser faqParser;
    private final VectorStore vectorStore;

    @EventListener(ApplicationReadyEvent.class)
    public void index() {
        long start = System.currentTimeMillis();
        List<Document> docs = faqParser.parse(faqLoader.content());
        vectorStore.add(docs);
        log.info("FAQ {}개 임베딩 완료 ({} ms)", docs.size(), System.currentTimeMillis() - start);
    }
}