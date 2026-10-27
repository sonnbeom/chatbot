package com.sb.chatbot.fqlbot.eval.service;

import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.service.ChatService;
import com.sb.chatbot.fqlbot.eval.dataset.EvalDatasetLoader;
import com.sb.chatbot.fqlbot.eval.domain.EvalResult;
import com.sb.chatbot.fqlbot.eval.domain.EvalRun;
import com.sb.chatbot.fqlbot.eval.dto.EvalCase;
import com.sb.chatbot.fqlbot.eval.repository.EvalResultRepository;
import com.sb.chatbot.fqlbot.eval.repository.EvalRunRepository;
import com.sb.chatbot.fqlbot.eval.scorer.KeywordScorer;
import com.sb.chatbot.fqlbot.eval.scorer.RetrievalScorer;
import com.sb.chatbot.fqlbot.eval.scorer.ScoreResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalRunner {

    private final EvalDatasetLoader datasetLoader;
    private final ChatService chatService;
    private final KeywordScorer keywordScorer;
    private final EvalRunRepository runRepository;
    private final EvalResultRepository resultRepository;

        // ── 시험 당시 설정을 기록하려고 yml 값을 읽어옴 (📋 이해 불필요) ──
    @Value("${spring.ai.ollama.chat.options.model}")
    private String chatModel;

    @Value("${spring.ai.ollama.embedding.options.model}")
    private String embeddingModel;

    @Value("${faqbot.prompt.system}")
    private String promptVersion;

    @Value("${faqbot.rag.top-k}")
    private int topK;

    @Value("${faqbot.rag.similarity-threshold}")
    private double similarityThreshold;

    @Value("${spring.ai.ollama.chat.options.temperature}")
    private double temperature;

    public Long run(String note, Integer limit){
        List<EvalCase> cases = datasetLoader.load();
        // 1. 시험지 읽기
        if (limit != null){
            cases = cases.stream().limit(limit).toList();
        }

        // 2. 이번 시럼 기록 시작
        EvalRun evalRun = new EvalRun(note, chatModel, embeddingModel, promptVersion, topK, similarityThreshold, temperature);
        EvalRun run = runRepository.save(evalRun);

        // 3. 문제마다 챗봇에게 묻고 채점하고 저장
        List<EvalResult> results = new ArrayList<>();
        for (int i = 0; i < cases.size(); i++) {
            EvalCase evalCase = cases.get(i);

            ChatResult answer  = ask(evalCase.question());
            Boolean hit = RetrievalScorer.hit(evalCase, answer.sources());
            ScoreResult keyword = keywordScorer.score(evalCase, answer.answer());
            results.add(resultRepository.save(new EvalResult(run, evalCase, answer, hit, keyword)));
            log.info("[{}/{}] {} 키워드={} 검색={} ({} ms)",
                    i + 1, cases.size(), evalCase.id(), keyword.pass(), hit, answer.elapsedMs());
        }
        run.finish(results);
        runRepository.save(run);
        return run.getId();
    }

    private ChatResult ask(String question){
        try {
            return chatService.ask(question);
        }catch (Exception e){
           log.warn("챗봇 호출 실패: {}", question, e);
           return new ChatResult("ERROR: " + e.getMessage(), List.of(), null, null, 0, 0, 0);
        }
    }
}
