package com.sb.chatbot.fqlbot.eval.dataset;

import com.sb.chatbot.fqlbot.eval.dto.EvalCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EvalDatasetLoader {

    private final JsonMapper jsonMapper;

    @Value("${faqbot.eval.dataset}")
    private Resource dataset;

    public List<EvalCase> load() {
        try (InputStream in = dataset.getInputStream()) {
            return List.of(jsonMapper.readValue(in, EvalCase[].class));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}