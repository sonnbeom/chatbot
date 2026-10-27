# TODO (갱신: 2026-10-23, 커밋 5a6333e)
## 이번에 한 일
- `faq.md`에 누리랩스 가상 사내 FAQ 30항목 작성 (휴가·근무·복지·장비·보안·출장·입사), `FaqLoader`로 로딩
- `ChatService`가 `{faq}`를 채운 시스템 프롬프트로 답하도록 v1 구현, `AiConfig`에 `ChatClient` 빈 등록, 프롬프트 파일명 수정
- 규칙·예시를 강화한 `faq-system-v2.st` 추가 (단, `application.yaml`의 `faqbot.prompt.system`은 아직 v1을 가리킴)
## 다음에 할 일
- [ ] `faqbot.prompt.system`을 `faq-system-v2.st`로 바꿔 v1과 같은 질문으로 답변 비교
- [ ] temperature 0과 0.8로 같은 질문 5번씩 보내고 차이 기록
- [ ] 개발 환경 설치 체크리스트 완료 (JDK 21, Ollama, 모델 pull)
## 최근 완료
- FAQ용 파일 골격 추가, application.yaml에 `num-ctx: 8192` 추가
- 모델을 exaone3.5:2.4b로 변경, num-predict 제한 및 repeat-penalty 상향
