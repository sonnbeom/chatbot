package com.sb.chatbot.fqlbot.eval.scorer;

import com.sb.chatbot.fqlbot.eval.dto.EvalCase;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class KeywordScorer {

    public ScoreResult score(EvalCase evalCase, String answer) {
        // 꼭 있어야 할 단어 중 빠진 것 찾기
        List<String> missing = evalCase.mustInclude().stream()
                .filter(group -> Arrays.stream(group.split("\\|")).noneMatch(answer::contains))
                .toList();

        // 있으면 안 되는 단어 중 들어간 것 찾기
        List<String> forbidden = evalCase.mustNotInclude().stream()
                .filter(answer::contains)
                .toList();

        boolean pass = missing.isEmpty() && forbidden.isEmpty();
        String reason = pass ? "OK" : "누락: %s / 금지어: %s".formatted(missing, forbidden);
        return new ScoreResult(pass, reason);
    }
}