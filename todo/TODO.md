# TODO (갱신: 2026-10-27, 커밋 f3331c2)
## 이번에 한 일
- 5점 루브릭을 담은 judge 프롬프트 `prompts/judge-v1.st` 작성, `faqbot.eval.judge.{model,prompt}` 설정 추가
- `LlmJudgeScorer` 골격(`BeanOutputConverter<JudgeVerdict>`)과 `JudgeVerdict`/`JudgeResult` 레코드 추가 (채점 메서드는 아직 없음)
## 다음에 할 일
- [ ] `LlmJudgeScorer`에 채점 메서드 구현: judge 프롬프트에 question/reference/answer를 채워 호출하고 `converter`로 JSON 파싱, 파싱 실패 시 `JudgeResult.error` 반환 (`Resource` import를 `jakarta.annotation`에서 `org.springframework.core.io`로 수정)
- [ ] `EvalResult`에 LLM 점수·근거·통과 여부 컬럼을 추가하고 `EvalRunner`에서 키워드 점수와 함께 저장
- [ ] 두 채점기가 엇갈린 문항 5개를 읽고 메모
- [ ] 문서에 없는 질문 5개로 "모른다"고 답하는지 손으로 확인 (3주차 남은 항목)
## 최근 완료
- `eval-cases.json` 30문항 + `EvalRunner`, 키워드·검색 채점 결과 H2 저장 및 `/eval/runs` API, 실행 리포트 생성
- `faq.md` 가상 사내 FAQ 30항목 작성, FAQ 전체를 시스템 프롬프트에 넣는 v1 구현, `faq-system-v2.st` 추가
- FAQ용 파일 골격 추가, application.yaml에 `num-ctx: 8192` 추가
- 모델을 exaone3.5:2.4b로 변경, num-predict 제한 및 repeat-penalty 상향
