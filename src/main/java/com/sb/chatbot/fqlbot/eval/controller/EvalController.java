package com.sb.chatbot.fqlbot.eval.controller;

import com.sb.chatbot.fqlbot.eval.dto.ResultView;
import com.sb.chatbot.fqlbot.eval.dto.RunRequest;
import com.sb.chatbot.fqlbot.eval.dto.RunSummary;
import com.sb.chatbot.fqlbot.eval.service.EvalReportService;
import com.sb.chatbot.fqlbot.eval.service.EvalRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eval/runs")
@RequiredArgsConstructor
public class EvalController {

    private final EvalRunner evalRunner;
    private final EvalReportService reportService;

    @PostMapping                                   // 시험 실행
    public RunSummary run(@RequestBody RunRequest request) {
        Long runId = evalRunner.run(request.note(), request.limit());
        return reportService.summary(runId);
    }

    @GetMapping                                    // 지금까지 본 시험 목록
    public List<RunSummary> list() {
        return reportService.summaries();
    }

    @GetMapping("/{runId}")                        // 시험 한 번의 성적표
    public RunSummary summary(@PathVariable Long runId) {
        return reportService.summary(runId);
    }

    @GetMapping("/{runId}/results")                // 문제별 답안과 채점 결과
    public List<ResultView> results(@PathVariable Long runId) {
        return reportService.results(runId);
    }
}