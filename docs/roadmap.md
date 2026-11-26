# 담당별 개발 순서와 GitHub 이슈

MVP 1에서는 회원가입부터 영화·회차·좌석 선택, 카드 모의 결제, 본인 예매 조회·취소까지 연결합니다. MVP 2에서는 시간대 필터, 심야 할인, 결제수단 선택과 응답 유실 대응을 추가합니다.

## 프론트 · Swing 화면

[프론트 이슈 모아 보기](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues?q=is%3Aissue+is%3Aopen+label%3Afrontend)

| 단계 | 작업 | 이슈 |
| --- | --- | --- |
| MVP 1 | Eclipse·FlatLaf 실행 구성과 공통 화면 | [#47][i47] |
| MVP 1 | 홈·영화 목록·상영 회차 화면 | [#49][i49] |
| MVP 1 | 인원·좌석 선택과 카드 모의 결제 화면 | [#51][i51] |
| MVP 1 | 회원가입·로그인과 예매 내역·취소 화면 | [#57][i57] |
| MVP 2 | 시간대 필터·할인 표시와 모의 결제수단 선택 | [#56][i56] |

## 백엔드·서버 · Java 소켓과 MySQL

[백엔드·서버 이슈 모아 보기](https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues?q=is%3Aissue+is%3Aopen+label%3Abackend)

| 단계 | 작업 | 이슈 |
| --- | --- | --- |
| MVP 1 | Java 소켓·MySQL 기반과 통신 규격 | [#48][i48] |
| MVP 1 | 영화·회차·잔여 좌석 조회 | [#58][i58] |
| MVP 1 | 회원 저장·로그인과 서버 로그인 상태 | [#50][i50] |
| MVP 1 | 모의 승인·예매 저장과 좌석 중복 방지 | [#52][i52] |
| MVP 1 | 본인 예매 내역·취소와 좌석 점유 해제 | [#54][i54] |
| MVP 2 | 시간대 필터·심야 할인과 모의 결제수단 저장 | [#53][i53] |
| MVP 2 | 응답 유실 후 예매 결과 조회와 중복 예매 방지 | [#55][i55] |

## 병행 개발과 연결

프론트는 [#47][i47], 백엔드는 [#48][i48]부터 시작합니다. 각 기능에서 화면이 보낼 입력과 서버가 돌려줄 성공·실패 예시를 먼저 공유합니다. 프론트는 예시로 화면을 만들고 백엔드는 실제 응답을 개발합니다.

| 기능 | 프론트 | 백엔드·서버 | 연결할 결과 |
| --- | --- | --- | --- |
| 영화·시간표 | [#49][i49] | [#58][i58] | 영화 목록·상세, 상영 회차와 잔여 좌석 |
| 좌석·예매 | [#51][i51] | [#58][i58], [#52][i52] | 좌석 상태, 서버 계산 금액, 예매번호·실패 원인 |
| 계정·내 예매 | [#57][i57] | [#50][i50], [#54][i54] | 회원가입·로그인, 내역 조회·취소 |
| 할인·결제 확장 | [#56][i56] | [#53][i53], [#55][i55] | 시간대 조회, 할인 금액, 결제수단, 예매 결과 재조회 |

프론트는 연결 후 가입 → 회차 → 좌석 → 모의 결제 → 내역 → 취소 순서로 화면을 확인합니다. 백엔드는 같은 좌석에 동시 요청이 와도 한 건만 확정되는지, 다른 회원의 내역에 접근할 수 없는지, 취소한 좌석을 다시 예매할 수 있는지 확인합니다. MVP 2에서는 심야 할인 경계와 응답이 유실된 뒤 같은 요청을 보내도 예매가 한 건만 남는지 확인합니다.

[i47]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/47
[i48]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/48
[i49]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/49
[i50]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/50
[i51]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/51
[i52]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/52
[i53]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/53
[i54]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/54
[i55]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/55
[i56]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/56
[i57]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/57
[i58]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/58
