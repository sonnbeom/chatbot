package com.sb.chatbot.fqlbot.eval.service;

import com.sb.chatbot.fqlbot.eval.domain.EvalResult;
import com.sb.chatbot.fqlbot.eval.domain.EvalRun;
import com.sb.chatbot.fqlbot.eval.dto.ResultView;
import com.sb.chatbot.fqlbot.eval.dto.RunSummary;
import com.sb.chatbot.fqlbot.eval.repository.EvalResultRepository;
import com.sb.chatbot.fqlbot.eval.repository.EvalRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalReportService {

    private final EvalRunRepository runRepository;
    private final EvalResultRepository resultRepository;

    public RunSummary summary(Long runId) {
        EvalRun run = runRepository.findById(runId)
                .orElseThrow(() -> new NoSuchElementException("실행 없음: " + runId));
        List<EvalResult> results = resultRepository.findByRunIdOrderByIdAsc(runId);

        // 분류별 통과율
        Map<String, Double> byCategory = results.stream().collect(Collectors.groupingBy(
                EvalResult::getCategory,
                TreeMap::new,
                Collectors.collectingAndThen(
                        Collectors.averagingDouble(r -> r.isKeywordPass() ? 1.0 : 0.0),
                        avg -> Math.round(avg * 1000) / 10.0)));

        return new RunSummary(
                run.getId(), run.getNote(),
                run.getChatModel(), run.getEmbeddingModel(), run.getPromptVersion(),
                run.getTopK(), run.getSimilarityThreshold(), run.getTemperature(),
                run.getTotalCases(),
                rate(run.getKeywordPassCount(), run.getTotalCases()),
                run.getRetrievalCaseCount() == 0 ? null
                        : rate(run.getRetrievalHitCount(), run.getRetrievalCaseCount()),
                byCategory,
                run.getAvgGenerationMs(),
                run.getTotalInputTokens(), run.getTotalOutputTokens(),
                run.getStartedAt(), run.getFinishedAt());
    }

    public List<RunSummary> summaries() {
        return runRepository.findAll().stream().map(r -> summary(r.getId())).toList();
    }

    public List<ResultView> results(Long runId) {
        return resultRepository.findByRunIdOrderByIdAsc(runId).stream().map(ResultView::from).toList();
    }

    private double rate(int count, int total) {
        return total == 0 ? 0 : Math.round(count * 1000.0 / total) / 10.0;  // 소수 첫째 자리 %
    }
}