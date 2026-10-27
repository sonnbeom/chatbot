package com.sb.chatbot.chat.dto.response;

import java.util.List;

public record ReportResult(String path, int total, int passed, List<Item> items) {

    // 질문 하나에 대한 요청/응답과 판정
    public record Item(String question, List<String> expected, String verdict, ChatResult response) {}
}
