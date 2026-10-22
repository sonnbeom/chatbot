# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Learning project following `LLM 챗봇 + 평가 하네스 학습 로드맵 (Java Spring).md` (8-week roadmap, written in Korean): build an FAQ RAG chatbot API, then an evaluation harness API that scores the chatbot and compares runs for regressions. The roadmap's weekly checklists define the intended scope/order of work. Code is written week by week alongside the roadmap.

Stack: Java 21, Spring Boot 4.1.x, Spring AI 2.0.x (BOM-managed), Ollama (local models only, no paid APIs), PostgreSQL + pgvector, Lombok, Gradle.

## Commands

```
docker compose up -d        # PostgreSQL+pgvector (host port 5433, db/user/password: chatbot)
ollama pull llama3.2:3b     # chat model (also nomic-embed-text for embeddings per roadmap)
./gradlew bootRun           # app on port 4200
./gradlew test
./gradlew test --tests com.sb.chatbot.ChatbotApplicationTests
```

`ChatbotApplicationTests` is a `@SpringBootTest` context-load test, so it needs Postgres and Ollama reachable (see `application.yaml`).

## Architecture

Single Spring Boot app, package `com.sb.chatbot`, feature-first layout: `chat/{controller,service,dto/request,dto/response}`. Currently `POST /chat` → `ChatController` → `ChatService.ask` → Spring AI `ChatClient` (Ollama), returning `ChatResult` (answer, input/output tokens, elapsed ms). Token/latency metrics in the response are deliberate: the roadmap's later weeks build observability and evaluation on them.

Config lives in `src/main/resources/application.yaml` (Ollama at `localhost:11434`, model `llama3.2:3b`, JPA `ddl-auto: update`, pgvector `initialize-schema: true`).

## Git hook

`.githooks/post-commit` (needs `git config core.hooksPath .githooks`) runs `claude -p` in the background after each commit, using `.githooks/todo-prompt.md` to tick completed checkboxes in the roadmap MD and rewrite `todo/TODO.md`. Its changes are left uncommitted in the working tree; log at `todo/.hook.log`. Expect roadmap/TODO diffs to appear after commits. The hook skips itself when `CLAUDE_TODO_HOOK` is set.
