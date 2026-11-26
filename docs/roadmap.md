# 개발 순서와 GitHub 이슈

MVP 1에서 가입·좌석 선택·모의 결제·본인 내역·취소까지 한 번의 예매가 이어져야 합니다. MVP 2에서는 할인과 선택 편의를 추가하고 재요청을 검증합니다. 각 이슈의 체크리스트를 기준으로 완료 여부를 확인합니다.

## MVP 1 · 기본 예매 흐름

| 순서 | 작업 | 이슈 |
| --- | --- | --- |
| 1 | Eclipse 실행 구성과 Swing 화면 전환 | [#47][i47] |
| 2 | Java 소켓 통신과 MySQL 연결 구성 | [#48][i48] |
| 3 | 영화 검색·상세와 상영 회차 조회 | [#49][i49] |
| 4 | 회원가입·로그인과 서버 로그인 상태 | [#50][i50] |
| 5 | 인원·좌석 선택과 예매 내용 확인 | [#51][i51] |
| 6 | 카드 모의 결제와 동시 좌석 예매 처리 | [#52][i52] |
| 7 | 본인 예매 조회·취소와 좌석 복구 | [#54][i54] |
| 8 | 기본 예매 흐름 통합 확인 | [#57][i57] |

Eclipse 실행 구성과 소켓·DB 기반은 병행할 수 있습니다. 이후 영화·회차 조회와 로그인, 좌석 선택, 모의 결제, 내역 순서로 연결합니다.

MVP 1이 완료되면 두 클라이언트가 같은 회차의 같은 좌석을 요청해 한 건만 성공하는지 확인합니다. 각 회원이 본인 예매만 조회할 수 있고, 취소 후에는 좌석이 다시 선택 가능해야 합니다.

## MVP 2 · 할인과 결제 선택 확장

| 작업 | 이슈 |
| --- | --- |
| 시간대 필터와 심야 할인 | [#53][i53] |
| 무통장입금·삼성페이·카카오페이 모의 결제수단 | [#56][i56] |
| 응답 유실 후 예매 결과 조회와 중복 요청 처리 | [#55][i55] |

심야 할인 경계, 예매 당시 금액 보존, 결제 실패 후 재시도와 재전송 시 중복 저장 방지를 확인합니다.

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
