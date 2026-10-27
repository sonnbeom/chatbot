package com.sb.chatbot.fqlbot.eval.scorer;

public record JudgeResult(Integer score, String reason, Boolean pass) {

    static JudgeResult of(int score, String reason, int passScore) {
        if (score < 1 || score > 5) {
            return error("점수 범위 밖: " + score);
        }
        return new JudgeResult(score, reason, score >= passScore);
    }

    static JudgeResult error(String message) {
        return new JudgeResult(null, "채점 오류: " + message, null);
    }
}