# JAVA프로그래밍응용 · Advanced Java Programming

수업 실습과 기말 프로젝트 학습 기록을 관리하는 저장소다.

## 기말 프로젝트: 포스트잇 협업 보드

여러 사람이 같은 보드에 글·이미지 카드를 붙이고, 카드를 옮기며 함께 정리한다. 변경을 실시간으로 동기화하고, 보드와 스냅샷을 저장·조회·공개한다.

개발 환경: Eclipse + Temurin JDK 21 + Maven + JavaFX. 서버 통신은 소켓, 저장은 JDBC를 사용한다. JavaFX 사용·1인 개발 허용 여부와 세부 기술 버전은 확인 항목이다.

## 시작하기

1. [따라 구현하기](docs/포스트잇보드_따라구현하기.md)의 단계 01에서 환경과 작은 실행 예제를 준비한다.
2. [#33 실행 기반](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/33)을 진행한다.
3. 성공하면 [#34 로컬 텍스트 카드](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/34)로 넘어간다.

현재 `app/`은 빈 Eclipse 프로젝트이며 Maven 설정·앱 코드는 아직 없다. 안내서에 설정·예제가 있지만 실제 구현이 완료됐다는 의미는 아니다.

## 문서와 개발 계획

- [기획서](docs/기말프로젝트_기획서.md)
- [PRD — 기능과 완료 기준](docs/기말프로젝트_PRD.md)
- [따라 구현하기 — 14단계 학습·구현 안내](docs/포스트잇보드_따라구현하기.md)
- [상위 이슈 #32 — 전체 작업 순서](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/32)
- [2026-2학기 통합 보드](https://github.com/users/HyeYeonLee0112/projects/9)
- [강의계획서](docs/강의계획서.md)

기존 릴레이 기획의 #20~#31은 기획 변경 사유로 닫았다. 새 개발 계획은 #32~#46이며, #33은 Todo, 후속 작업은 Backlog에서 관리한다.

## 학습 방식

수업 기능 복습 → 아주 작은 예제 → 기능 결합 → 예외·경계값 확인 → 리팩터링·회고 순으로 진행한다. Swing 수업 실습과 JavaFX 기말 프로젝트를 구분한다.

AI는 개념 설명·API 사용·힌트·최소 예제·코드 리뷰에 활용한다. 핵심 구현과 제출 설명은 직접 작성한다. 각 문서는 구현을 위한 작업 초안이며 제출용 최종본이 아니다.
