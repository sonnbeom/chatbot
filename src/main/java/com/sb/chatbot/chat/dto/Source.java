package com.sb.chatbot.chat.dto;


import org.springframework.ai.document.Document;

public record Source(String faqId, String question, Double score) {

    public static Source from(Document doc) {
        return new Source(
                (String) doc.getMetadata().get("faqId"),
                (String) doc.getMetadata().get("question"),
                doc.getScore()
        );
    }
}