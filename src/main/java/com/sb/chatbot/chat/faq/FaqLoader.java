package com.sb.chatbot.chat.faq;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FaqLoader {

    @Value("classpath:faq/faq.md")
    private Resource faqFile;

    private String content;

    @PostConstruct
    void load() throws IOException {
        this.content = faqFile.getContentAsString(StandardCharsets.UTF_8);
    }

    public String content() {
        return content;
    }
}