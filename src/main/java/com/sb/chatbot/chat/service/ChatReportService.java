package com.sb.chatbot.chat.service;

import com.sb.chatbot.chat.dto.Source;
import com.sb.chatbot.chat.dto.response.ChatResult;
import com.sb.chatbot.chat.dto.response.ReportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// questions.txt의 질문을 전부 챗봇에 보내고, 질문/검색 결과/답변을 md 파일 하나로 남긴다
@Service
@RequiredArgsConstructor
public class ChatReportService {

    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
    private static final DateTimeFormatter TITLE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ChatService chatService;

    @Value("${faqbot.report.questions}")
    private Resource questions;

    @Value("${faqbot.report.dir}")
    private String reportDir;

    @Value("${faqbot.prompt.system}")
    private Resource systemPrompt;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    @Value("${faqbot.rag.top-k}")
    private int topK;

    @Value("${faqbot.rag.similarity-threshold}")
    private double similarityThreshold;

    record Case(String question, List<String> expected) {}

    enum Verdict {
        PASS("✅ 적중"), PARTIAL("🔶 일부"), MISS("❌ 놓침"), NONE("✅ 검색 없음"), CHECK("⚠️ 점수 확인");

        final String label;

        Verdict(String label) {
            this.label = label;
        }

        boolean passed() {
            return this == PASS || this == NONE;
        }
    }

    public ReportResult run() throws IOException {
        List<Case> cases = loadCases();
        LocalDateTime now = LocalDateTime.now();

        StringBuilder table = new StringBuilder("| # | 질문 | 기대 | 검색 결과 (점수) | 판정 | 전체 ms |\n|---|---|---|---|---|---|\n");
        StringBuilder details = new StringBuilder();
        int passed = 0;

        for (int i = 0; i < cases.size(); i++) {
            Case c = cases.get(i);
            ChatResult result = chatService.ask(c.question());
            Verdict verdict = judge(c.expected(), result.sources());
            if (verdict.passed()) passed++;

            String expected = c.expected().isEmpty() ? "없음" : String.join(", ", c.expected());
            String sources = formatSources(result.sources(), c.expected());

            table.append("| %d | %s | %s | %s | %s | %d |\n".formatted(
                    i + 1, cell(c.question()), expected, sources, verdict.label, result.elapsedMs()));

            details.append("""
                    ## %d. %s

                    - 기대: %s · 판정: %s
                    - 검색: %s
                    - 토큰: 입력 %s / 출력 %s · 검색 %dms / 생성 %dms

                    %s

                    """.formatted(
                    i + 1, c.question(), expected, verdict.label, sources,
                    result.inputTokens(), result.outputTokens(), result.retrievalMs(), result.generationMs(),
                    quote(result.answer())));
        }

        String markdown = """
                # FAQ 챗봇 점검 — %s

                - 모델: `%s` · 프롬프트: `%s` · top-k %d · threshold %s
                - 통과: **%d / %d**

                %s
                ---

                %s""".formatted(
                now.format(TITLE_TIME), model, systemPrompt.getFilename(), topK, similarityThreshold,
                passed, cases.size(), table, details);

        Path dir = Path.of(reportDir);
        Files.createDirectories(dir);
        Path file = dir.resolve(now.format(FILE_TIME) + ".md");
        Files.writeString(file, markdown, StandardCharsets.UTF_8);

        return new ReportResult(file.toAbsolutePath().normalize().toString(), cases.size(), passed);
    }

    private List<Case> loadCases() throws IOException {
        return questions.getContentAsString(StandardCharsets.UTF_8).lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(line -> {
                    String[] parts = line.split("\\|", 2);
                    List<String> expected = parts.length < 2 ? List.of() : Arrays.stream(parts[1].split(","))
                            .map(String::trim)
                            .filter(id -> !id.isEmpty())
                            .toList();
                    return new Case(parts[0].trim(), expected);
                })
                .toList();
    }

    // 기대 FAQ가 있으면 상위 결과에 다 들어왔는지, 없으면 검색이 비었는지(threshold에 걸러졌는지) 본다
    private Verdict judge(List<String> expected, List<Source> sources) {
        if (expected.isEmpty()) {
            return sources.isEmpty() ? Verdict.NONE : Verdict.CHECK;
        }
        List<String> found = sources.stream().map(Source::faqId).toList();
        long hits = expected.stream().filter(found::contains).count();
        if (hits == expected.size()) return Verdict.PASS;
        return hits > 0 ? Verdict.PARTIAL : Verdict.MISS;
    }

    // 기대한 FAQ는 굵게 표시
    private String formatSources(List<Source> sources, List<String> expected) {
        if (sources.isEmpty()) return "(없음)";
        return sources.stream()
                .map(s -> {
                    String id = expected.contains(s.faqId()) ? "**" + s.faqId() + "**" : s.faqId();
                    return "%s (%.3f)".formatted(id, s.score());
                })
                .collect(Collectors.joining(", "));
    }

    private String cell(String text) {
        return text.replace("|", "\\|").replace("\n", " ");
    }

    private String quote(String text) {
        return text.strip().lines().map(line -> "> " + line).collect(Collectors.joining("\n"));
    }
}
