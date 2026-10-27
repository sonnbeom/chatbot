package com.sb.chatbot.fqlbot.eval.scorer;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmJudgeScorer {

    private static final int PASS_SCORE = 4;

    private final ChatClient chatClient;
    private final BeanOutputConverter<JudgeVerdict> converter =
            new BeanOutputConverter<>(JudgeVerdict.class);

    @Value("${faqbot.eval.judge.model}")
    private String judgeModel;

    @Value("${faqbot.eval.judge.prompt}")
    private Resource judgePrompt;

}
