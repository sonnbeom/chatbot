# TODO (갱신: 2026-10-27, 커밋 69edf9f)
## 이번에 한 일
- `eval-cases.json` 30문항(JSON) + `EvalDatasetLoader`, 전 문항을 실행·채점하는 `EvalRunner` 구현
- `KeywordScorer`·`RetrievalScorer` 채점 후 `EvalRun`/`EvalResult`를 H2에 저장, `/eval/runs` 실행·목록·요약·결과 조회 API 추가
- `ChatReportService`로 `reports/`에 실행 리포트 3건 생성
## 다음에 할 일
- [ ] 문서에 없는 질문 5개로 "모른다"고 답하는지 손으로 확인 (3주차 남은 항목)
- [ ] 루브릭을 담은 judge 프롬프트 작성 + JSON 응답 파싱하는 LLM 채점기
- [ ] `EvalResult`에 키워드 점수와 LLM 점수를 함께 저장
- [ ] 두 채점기가 엇갈린 문항 5개를 읽고 메모
## 최근 완료
- `faq.md` 가상 사내 FAQ 30항목 작성, FAQ 전체를 시스템 프롬프트에 넣는 v1 구현, `faq-system-v2.st` 추가
- FAQ용 파일 골격 추가, application.yaml에 `num-ctx: 8192` 추가
- 모델을 exaone3.5:2.4b로 변경, num-predict 제한 및 repeat-penalty 상향
