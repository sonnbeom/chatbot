# TODO (갱신: 2026-10-22, 커밋 23c61c1)
## 이번에 한 일
- 모델을 exaone3.5:2.4b로 변경, num-predict 제한 및 repeat-penalty 상향 (application.yaml, ChatService)
- compose.yaml 삭제, build.gradle/CLAUDE.md/.gitignore 정리
## 다음에 할 일
- [ ] 개발 환경 설치 체크리스트 완료 (JDK 21, Ollama, 모델 pull)
- [ ] 가상의 사내 FAQ 20~30항목 작성
- [ ] FAQ 전체를 시스템 프롬프트에 넣어 답하는 v1 구현
- [ ] temperature 0과 0.8로 같은 질문 5번씩 보내고 차이 기록
## 최근 완료
- 커밋 시 to do 자동 갱신 후킹 추가
