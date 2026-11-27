# 프로젝트 구성

프로그램을 Eclipse에서 Java로 개발합니다. 사용자는 Swing 화면에서 영화와 회차를 찾고, 좌석을 선택해 모의 결제로 예매를 확정합니다.

## 프로그램 구성

`Swing 클라이언트 → Java 소켓 서버 → JDBC(MySQL Connector/J) → MySQL`

| 구성 | 맡는 일 |
| --- | --- |
| Swing 클라이언트 | FlatLaf로 입력창과 선택 메뉴를 꾸밉니다. 홈·영화·예매 메뉴, 로그인, 좌석 격자, 결제와 본인 예매 내역을 표시합니다. `CardLayout`으로 화면을 전환하고 통신 응답은 `SwingWorker`로 기다립니다. |
| Java 소켓 서버 | 회원 상태와 예매 소유자를 확인합니다. 영화·회차·잔여 좌석을 조회하고, 요청을 검증한 뒤 모의 승인·예매 확정·취소 결과를 반환합니다. |
| MySQL | 회원, 영화, 회차, 좌석, 예매와 좌석 점유 상태를 저장합니다. JDBC 연결은 서버에서 관리합니다. |

## 프론트와 백엔드의 연결

프론트 담당자는 Swing 화면, 입력 처리와 화면 전환을 맡습니다. 백엔드 담당자는 Java 소켓 서버, MySQL, Swing이 호출할 Java 연결 코드를 맡습니다. 화면에서는 Java 연결 코드를 `SwingWorker`에서 호출하고 결과에 따라 화면을 갱신합니다.

[#48][i48]에서 호출 방식과 요청·응답 항목을 합의합니다. 회원·예매에서 필요한 입력값, 영화·회차·좌석의 식별값, 결과 데이터와 화면에 전달할 오류 코드를 정하고, 성공·실패 응답 예시를 공유합니다. 화면과 서버는 이 예시를 기준으로 병행 개발합니다.

## Java 패키지 구성

Swing 클라이언트와 Java 서버를 큰 패키지로 구분하고, 각 패키지 안에서 관련 기능의 코드를 묶습니다. MySQL은 데이터 계층이고, Java 서버가 공용 DB 연결 코드로 접근합니다.

```text
cinema/
  client/              Swing 앱 시작과 공통 창
    auth/              로그인·회원가입 화면과 화면 검증용 응답
    transport/         Swing에서 호출할 인터페이스와 소켓 연결
  server/              서버 실행과 DB 접속 점검
    auth/              회원 요청 처리, 회원가입·로그인 로직, 회원 조회·저장
    db/                여러 서버 기능이 사용하는 MySQL 연결
  dto/
    auth/              화면과 서버가 주고받는 회원 요청·응답
```

| 패키지 | 담당 | 회원 기능 구현 시 둘 클래스 |
| --- | --- | --- |
| `cinema.client.auth` | 프론트 | `LoginPanel`, `SignUpPanel`, `MockAuthClient` |
| `cinema.client.transport` | 백엔드 | `AuthClient`, `SocketAuthClient` |
| `cinema.server.auth` | 백엔드 | `AuthRequestHandler`, `AuthService`, `MemberDao` |
| `cinema.server.db` | 백엔드 | `DbConnection` |
| `cinema.dto.auth` | 프론트와 형식을 합의하고 백엔드가 작성 | `LoginRequest`, `LoginResult`, `SignUpRequest`, `SignUpResult` |

영화 조회와 예매를 구현할 때도 관련 파일을 클라이언트와 서버의 기능별 패키지에 모읍니다. 예를 들어 영화 화면은 `cinema.client.movie`, 영화 서버 처리는 `cinema.server.movie`에 둡니다.

## 예매 처리 규칙

1. 영화·회차·인원·좌석을 고릅니다. 일반·청소년·경로·우대 합계는 1~6명이며, 인원과 선택 좌석 수가 맞아야 합니다.
2. 예매를 진행하려면 로그인해야 합니다. 로그인 뒤에는 고른 회차와 좌석을 복원합니다.
3. 서버가 회차·인원·좌석·금액을 검증합니다. 모의 승인 후 예매·좌석을 하나의 트랜잭션으로 저장합니다.
4. 좌석 점유에는 `(회차 ID, 좌석 ID)` 중복 방지 조건을 둡니다. 이미 점유된 좌석이 있으면 저장을 되돌리고 다시 선택하도록 안내합니다.
5. 취소 요청은 소유자와 예매 상태를 확인한 뒤 처리합니다. 좌석 점유를 해제하고 취소 기록은 내역에 남깁니다.

MVP 1의 기본요금은 시연용으로 1인 12,000원입니다. MVP 2에서는 22:00 이상부터 다음 날 06:00 미만까지 시작하는 회차에 심야 10% 할인을 적용합니다. 예매 당시 금액과 수단을 저장합니다. 실제 카드 승인·은행 입금·간편결제 연동과 환불, 간식 구매는 작업 범위에서 제외합니다.

## 개발 설정

클라이언트는 Eclipse에서 실행합니다. 서버 주소·포트와 MySQL 접속 설정은 팀 시연 환경에 맞춥니다. 저장소에는 예시 설정을 두고 실제 DB 계정 정보는 로컬에 둡니다. 실행 방법과 시연 데이터는 [#47][i47]와 [#48][i48]에서 준비합니다.

[i47]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/47
[i48]: https://github.com/HyeYeonLee0112/AdvancedJavaProgramming/issues/48
