package com.sb.chatbot.chat.controller;

import com.sb.chatbot.chat.dto.request.ChatRequest;
import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    @PostMapping
    public ChatResult chat(@RequestBody ChatRequest request){
        ChatResult chatResult = chatService.ask(request);
        return chatResult;
    }
}
