# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Learning project following `LLM 챗봇 + 평가 하네스 학습 로드맵 (Java Spring).md` (8-week roadmap, written in Korean): build an FAQ RAG chatbot API, then an evaluation harness API that scores the chatbot and compares runs for regressions. The roadmap's weekly checklists define the intended scope/order of work. Code is written week by week alongside the roadmap.

Stack: Java 21, Spring Boot 4.1.x, Spring AI 2.0.x (BOM-managed), Ollama (local models only, no paid APIs), H2 (file DB) + Spring AI `SimpleVectorStore` (in-memory), Lombok, Gradle. pgvector/PostgreSQL is deferred until the FAQ grows past ~100 entries; swap in via the `VectorStore` interface then.

## Commands

```
ollama pull exaone3.5:2.4b  # chat model
ollama pull bge-m3          # embedding model (nomic-embed-text is the roadmap default, kept for comparison)
./gradlew bootRun           # app on port 4200
./gradlew test
./gradlew test --tests com.sb.chatbot.ChatbotApplicationTests
```

`ChatbotApplicationTests` is a `@SpringBootTest` context-load test, so it needs Ollama reachable (H2 is embedded) (see `application.yaml`).

## Architecture

Single Spring Boot app, package `com.sb.chatbot`, feature-first layout: `chat/{controller,service,dto/request,dto/response}`. Currently `POST /chat` → `ChatController` → `ChatService.ask` → Spring AI `ChatClient` (Ollama), returning `ChatResult` (answer, input/output tokens, elapsed ms). Token/latency metrics in the response are deliberate: the roadmap's later weeks build observability and evaluation on them.

Config lives in `src/main/resources/application.yaml` (Ollama at `localhost:11434`, chat model `exaone3.5:2.4b`, embedding model `bge-m3`, H2 file DB at `./data/faqbot`, JPA `ddl-auto: update`). Vector store is a `SimpleVectorStore` bean defined in `chat/config/AiConfig` (no auto-configuration). If Ollama returns `HTTP 404 model '...' not found`, the configured model hasn't been pulled.

## Git hook

`.githooks/post-commit` (needs `git config core.hooksPath .githooks`) runs `claude -p` in the background after each commit, using `.githooks/todo-prompt.md` to tick completed checkboxes in the roadmap MD and rewrite `todo/TODO.md`. Its changes are left uncommitted in the working tree; log at `todo/.hook.log`. Expect roadmap/TODO diffs to appear after commits. The hook skips itself when `CLAUDE_TODO_HOOK` is set.
