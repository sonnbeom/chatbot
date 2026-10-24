package com.sb.chatbot.chat.controller;

import com.sb.chatbot.chat.dto.Source;
import com.sb.chatbot.chat.dto.request.ChatRequest;
import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    @PostMapping
    public ChatResult chat(@RequestBody ChatRequest request){
        ChatResult chatResult = chatService.ask(request.question());
        return chatResult;
    }
    @GetMapping("/search")
    public List<Source> search(@RequestParam String q) {
        return chatService.search(q);
    }

}
