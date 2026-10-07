# #32 · 전체 개발 계획과 완료 판정

한 번에 서비스 전체를 만들지 않는다. 화면에서 카드 하나를 다룬 뒤 통신·저장·권한을 붙이고, 마지막에 보관함과 캐시를 연결한다.

상태: 학습·구현 계획. 구현·평가 승인·시연 완료 보고가 아니다.
연결: [#32](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/32) · [PRD](../기말프로젝트_PRD.md) · [기획서](../기말프로젝트_기획서.md) · [문서 목록](../포스트잇보드_따라구현하기.md)

## 1. 제품과 이번 작업의 범위

여러 사람이 보드에 글·이미지 포스트잇을 붙이고 정리한다. 변경을 저장·동기화하고 삭제 전 최종 모습을 보관한다. 소유자가 스냅샷을 공개할 수 있다.

참고 서비스의 B-01~B-13은 필수다. 학습 확장은 JDBC 저장, 실패/재접속 복구, 버전 충돌, 화면 작업 분리, 썸네일 캐시다. 카드 색상은 후보이며 채팅·게임·그림판·AI 생성·무한 캔버스는 확정하지 않는다.

현재 app은 빈 Eclipse 프로젝트다. 이번에 만든 문서는 직접 구현하기 위한 안내서이며 기능을 구현한 결과는 아니다.

## 2. 환경과 확인할 선택

| 항목 | 현재 기준 | 언제 확인 |
|---|---|---|
| IDE·JDK·빌드·화면 | Eclipse·Temurin 21·Maven·JavaFX | #33 |
| JavaFX·1인 개발 허용 | 교수자 확인 필요 | 착수 전/가능한 빠르게 |
| JavaFX 버전 | 최소 예제는 21.0.12, 최종 선택 미정 | #33 실행 후 기록 |
| DB·드라이버 | 미정 | #38 |
| 이미지 한도·보드 크기 | 미정 | #35·#36 |
| 해시 API·계정 규칙 | 미정 | #39 |
| 코드 변경 후 기존 참가자 | 미정 | #40 |
| 스냅샷 렌더링·파일 실패 정리 | 미정 | #43 |
| 제출·배포 방식 | 교수자 요구 확인 | #46 |

강의 소개의 RMI는 과목 범위에 언급됐지만 현재 PRD의 필수 구현에는 확정하지 않았다. 반드시 사용해야 하는지 교수자에게 확인한다. 필요하면 원격 호출의 책임을 명확히 정해 별도 이슈로 계획하며 소켓과 중복 구현을 무조건 추가하지 않는다. 현재 안내서가 모든 강의 기술의 적용 확정을 뜻하지 않는다.

## 3. 개발 순서와 문서

| 단계 | 이슈 | 독립 문서 |
|---|---|---|
| 01 | [#33](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/33) | [#33 Maven·JavaFX 실행 기반](issue-33-setup.md) |
| 02 | [#34](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/34) | [#34 로컬 텍스트 포스트잇 CRUD](issue-34-local-notes.md) |
| 03 | [#35](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/35) | [#35 카드 드래그·겹침 순서](issue-35-drag-order.md) |
| 04 | [#36](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/36) | [#36 이미지 포스트잇·미리보기](issue-36-image-notes.md) |
| 05 | [#37](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/37) | [#37 소켓 메시지·백그라운드 수신](issue-37-socket-workers.md) |
| 06 | [#38](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/38) | [#38 JDBC 저장·카드 복원](issue-38-jdbc-storage.md) |
| 07 | [#39](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/39) | [#39 회원 인증·로그아웃·서버 세션](issue-39-auth-sessions.md) |
| 08 | [#40](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/40) | [#40 보드 관리·공유 코드·편집 권한](issue-40-boards-permissions.md) |
| 09 | [#41](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/41) | [#41 보드별 동기화·동시 수정 충돌](issue-41-sync-conflicts.md) |
| 10 | [#42](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/42) | [#42 재접속·보드 삭제·접속 권한 변경](issue-42-reconnect-lifecycle.md) |
| 11 | [#43](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/43) | [#43 보드 스냅샷·삭제 전 보관](issue-43-snapshot-preservation.md) |
| 12 | [#44](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/44) | [#44 스냅샷 공개 갤러리·삭제](issue-44-gallery-access.md) |
| 13 | [#45](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/45) | [#45 썸네일 LRU 캐시](issue-45-thumbnail-cache.md) |
| 14 | [#46](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/46) | [#46 통합 검증·실행 안내·5분 시연](issue-46-integration-demo.md) |

번호는 추천 읽기 순서다. 각 문서/이슈의 선행 조건이 실제 착수 조건이다. 예를 들어 #37은 카드 전체보다 먼저 통신 실습을 할 수 있지만, #41은 카드·통신·권한 기반이 함께 필요하다.

## 4. 파일과 객체 관계

아래는 앞으로 만들 책임의 제안이다. 현재 이 구조가 구현됐다고 가정하지 않는다.

```text
사용자 행동
  → JavaFX View: 표시와 입력
  → Controller: 입력 의도·요청과 결과의 화면 연결
  → SocketClient: 요청 전달·결과 수신
  → 서버 Service: 인증·권한·한도·버전 검사
  → Repository/JDBC: 저장과 조회
  → 서버 승인 결과: 같은 보드 구독 연결에 전달
  → Controller → JavaFX 화면 갱신

이미지: ImageStore/SnapshotRenderer → 저장된 이미지 식별자
보관함: 권한/버전 확인 → ThumbnailService → LRU 캐시 또는 다운로드
```

View에는 SQL을, Repository에는 JavaFX 부품을 넣지 않는다. 서버가 승인한 결과만 저장 성공으로 보여준다. 통신·DB·이미지 대기는 화면 스레드에서 하지 않는다.

## 5. 매 이슈의 작업 방식

1. 복습: 이슈의 문제가 무엇인지 자기 말로 설명한다.
2. 최소 실습: 안내서의 작은 API 예제를 실행한다.
3. 결합: 본인이 서비스 코드를 작성한다. 한 동작을 추가한 뒤 확인한다.
4. 실패 검사: 경계값·거절 요청·끊김·부분 저장 실패를 재현한다.
5. 책임 분리: 동작한 뒤 계산·검사·표시·저장을 적절한 파일로 나눈다.
6. 회고/리뷰: 파일·테스트·오류·선택 이유를 남긴다.

문서나 빈 클래스가 생긴 것만으로 이슈를 완료하지 않는다. 핵심 구현과 제출 문서는 직접 작성하고 AI는 개념·API·힌트·최소 예제·내 코드 리뷰에 활용한다.

## 6. 단계별 중간 확인

| 묶음 | 실행으로 보여줄 것 | 아직 안 되는 것 |
|---|---|---|
| #33~#36 | 한 PC의 글·이미지 카드와 이동 | 다른 PC 동기화·영구 저장 |
| #37~#40 | 요청 왕복·저장·인증·보드 권한 | 동시 수정의 안전한 통합 |
| #41~#42 | 두 화면 동기화·충돌·최신 재접속 | 최종 스냅샷 기반 삭제 |
| #43~#45 | 보관·공개·이미지 접근·캐시 | 배포/전체 검증 완료 |
| #46 | 전체 검증과 새 폴더 실행·5분 시연 | 실행하지 않은 성능 주장 |

#42의 보드 삭제 실행은 #43의 보관 성공 조건을 연결한 뒤에 완성한다. 중간에 삭제 수신 처리만 동작한다고 제품의 삭제 기능이 완료됐다고 쓰지 않는다.

## 7. 선택·검증을 기록하는 방식

```text
이슈 / 코드 버전:
선택할 문제:
검토한 대안:
내 선택과 이유:
영향받는 파일·테스트:
실제 재현 결과:
남은 실패·미확인:
다음 이슈로 넘길 규칙:
```

GitHub 상태는 실제 착수/완료에 맞춰 바꾼다. 안내서 작성으로 개발 상태를 Done으로 바꾸지 않는다. 다른 과목 이슈와 기존 수업 자료를 변경하지 않는다.

## 8. 전체 완료 기준

- [ ] B-01~B-13의 사용자 동작을 모두 검증한다.
- [ ] L-01~L-07의 저장·실패·재접속·화면 응답을 검증한다.
- [ ] C-01~C-05의 크기·개수·키·권한·무효화·측정을 검증한다.
- [ ] V-01~V-14의 조건·실제 결과·증거·재검증이 있다.
- [ ] 새 작업 폴더에서 실행 안내로 서버와 클라이언트를 실행한다.
- [ ] 실제 사용자 흐름 시연을 300초 안에 마친다.
- [ ] 직접 작성한 핵심 코드의 책임·객체 관계·실행 흐름을 설명한다.
- [ ] 미정 사항·교수자 확인·구현/검증 한계를 숨기지 않는다.

지금 첫 행동: [#33 Maven·JavaFX 실행 기반](issue-33-setup.md)의 Java 버전 확인과 버튼 한 개 실행.
