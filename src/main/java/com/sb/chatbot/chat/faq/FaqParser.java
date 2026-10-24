package com.sb.chatbot.chat.faq;

import org.springframework.stereotype.Component;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// FQA를 ### Q01 단위로 잘라서 문서 하나씩으로 만드는 과정, 청킹이며 FAQ는 질문 하나가 독립된 더어리라 항목 단위로 자르는 게 베스트
@Component
public class FaqParser {

    private static final Pattern ITEM = Pattern.compile("^### (Q\\d+)\\. (.+)$");

    public List<Document> parse(String markdown) {
        List<Document> docs = new ArrayList<>();
        String category = "";
        String id = null;
        String question = null;
        StringBuilder answer = new StringBuilder();

        for (String line : markdown.split("\\R")) {
            Matcher item = ITEM.matcher(line);
            boolean isItem = item.matches();
            boolean boundary = isItem || line.startsWith("## ") || line.equals("---");

            if (boundary && id != null) {
                docs.add(toDocument(category, id, question, answer));
                id = null;
                answer.setLength(0);
            }

            if (line.startsWith("## ")) {
                category = line.substring(3).trim();
            } else if (isItem) {
                id = item.group(1);
                question = item.group(2).trim();
            } else if (id != null && !line.isBlank()) {
                answer.append(line).append('\n');
            }
        }
        if (id != null) {
            docs.add(toDocument(category, id, question, answer));
        }
        return docs;
    }

    private Document toDocument(String category, String id, String question, StringBuilder answer) {
        String text = "[%s] %s%n%s".formatted(category, question, answer.toString().trim());
        return new Document(text, Map.of(
                "faqId", id,
                "category", category,
                "question", question
        ));
    }
}