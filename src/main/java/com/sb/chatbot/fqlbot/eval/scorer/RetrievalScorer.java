package com.sb.chatbot.fqlbot.eval.scorer;


import com.sb.chatbot.chat.dto.Source;
import com.sb.chatbot.fqlbot.eval.dto.EvalCase;

import java.util.List;
//"맞는 FAQ를 찾았나"
public final class RetrievalScorer {

    private RetrievalScorer() {}

    /** 정답 FAQ가 하나라도 검색됐으면 true. 정답 FAQ가 없는 문항(out_of_doc)은 null */
    public static Boolean hit(EvalCase evalCase, List<Source> sources) {
        if (evalCase.expectedFaqIds().isEmpty()) {
            return null;
        }
        return sources.stream().anyMatch(s -> evalCase.expectedFaqIds().contains(s.faqId()));
    }
}