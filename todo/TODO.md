# TODO (갱신: 2026-10-22, 커밋 e73dd49)
## 이번에 한 일
- FAQ용 파일 골격 추가: 빈 `faq/faq.md`, `prompts/faq-system-v1.st` (파일명이 `faq-system-v1.stfaq-system-v1.st`로 중복돼 있음)
- application.yaml에 `num-ctx: 8192` 추가, ChatService에 Usage 주석 추가
## 다음에 할 일
- [ ] 개발 환경 설치 체크리스트 완료 (JDK 21, Ollama, 모델 pull)
- [ ] `faq.md`에 가상의 사내 FAQ 20~30항목 작성 (현재 비어 있음)
- [ ] 프롬프트 파일명 `faq-system-v1.st`로 수정 후 FAQ 전체를 시스템 프롬프트에 넣어 답하는 v1 구현
- [ ] temperature 0과 0.8로 같은 질문 5번씩 보내고 차이 기록
## 최근 완료
- 모델을 exaone3.5:2.4b로 변경, num-predict 제한 및 repeat-penalty 상향
