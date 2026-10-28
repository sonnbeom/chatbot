package com.sb.chatbot.fqlbot.eval.scorer;

import com.sb.chatbot.fqlbot.eval.dto.EvalCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource; // ✅ jakarta.annotation.Resource → 이걸로 변경
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmJudgeScorer {

    private static final int PASS_SCORE = 4;

    private final ChatClient chatClient;
    //BeanOutputConverter<JudgeVerdict>: Spring AI가 제공하는 클래스로, LLM이 돌려준 JSON 문자열을 Java 객체로 바꿔줍니다.
    // 여기서는 그 객체가 JudgeVerdict입니다(score, reason을 가진 record).
    private final BeanOutputConverter<JudgeVerdict> converter = new BeanOutputConverter<>(JudgeVerdict.class);

    @Value("${faqbot.eval.judge.model}")
    private String judgeModel;

    // 파일 리소스(예: classpath:prompts/judge.st)를 Spring이 Resource 객체로 주입
    @Value("${faqbot.eval.judge.prompt}")
    private Resource judgePrompt;

    public JudgeResult score(EvalCase evalCase, String answer) {
        try {
            String json = chatClient.prompt()
                    // ① 기준표의 {question}, {reference}, {answer} 자리를 채움
                    .user(u -> u.text(judgePrompt) //프롬프트용 파일 가져오고
                            .param("question", evalCase.question()) // 템플릿 안의 값을 넣어줌
                            .param("reference", evalCase.referenceAnswer())
                            .param("answer", answer))
                    // ② 채점용 설정: 채점 모델, temperature 0, JSON 모양 강제
                    .options(OllamaChatOptions.builder() //이번 호출에만 적용할 옵션입니다. application.yml의 기본 설정을 이 요청에 한해 덮어씁니다.
                            .model(judgeModel) // 챗봇이 쓰는 모델이 아니라 채점 전용 모델로 호출합니다
                            .temperature(0.0) //무작위성을 최소화합니다. 같은 답안이면 같은 점수가 나오게 해서 채점 일관성을 높이려는 설정입니다
                            .outputSchema(converter.getJsonSchema())) // JudgeVerdict 모양의 JSON으로만 답하도록 강제
                    .call() // 동기호출
                    .content(); // 응답 텍스트(JSON 문자열)만 꺼냄

            // ③ JSON → JudgeVerdict 객체
            JudgeVerdict verdict = converter.convert(json);
            return JudgeResult.of(verdict.score(), verdict.reason(), PASS_SCORE);

        } catch (Exception e) {
            log.warn("LLM 채점 실패: {}", evalCase.id(), e);
            return JudgeResult.error(e.getMessage());
        }
    }

    public String model() {
        return judgeModel;
    }
}