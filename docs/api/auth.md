# 회원가입과 로그인 요청·응답 규칙

Swing 화면은 `AuthClient`의 `signUp()`과 `login()`을 호출합니다. 화면이 결과를 받으면 먼저 `success()`를 보고, 실패했으면 `errorCode()`에 해당하는 안내를 표시합니다.

## 요청과 성공 응답

| 동작 | 요청 DTO와 입력값 | 성공 응답 |
| --- | --- | --- |
| 회원가입 | `SignUpRequest`에 이메일 `email`, 비밀번호 `password`, 표시 이름 `displayName` | `SignUpResult`에 `success=true`와 생성된 `memberId`. 화면은 로그인으로 이동합니다. |
| 로그인 | `LoginRequest`에 이메일 `email`과 비밀번호 `password` | `LoginResult`에 `success=true`, `memberId`, `displayName`, `sessionToken`. 화면은 로그인 성공 결과를 메인 창에 전달합니다. |

두 결과 모두 성공 시 `errorCode`는 `null`입니다. 실패 시 `success=false`와 `AuthErrorCode`가 들어가고, 회원 정보와 세션 토큰은 `null`입니다.

## 실패 코드와 화면 동작

| 코드 | 만드는 쪽 | 발생 조건 | 화면 동작 |
| --- | --- | --- | --- |
| `EMAIL_ALREADY_EXISTS` | 서버 | 이미 가입된 이메일로 회원가입 요청 | “이미 가입된 이메일이에요”를 표시하고 이메일 입력칸을 확인할 수 있게 합니다. |
| `INVALID_CREDENTIALS` | 서버 | 이메일과 비밀번호로 로그인할 계정을 확인할 수 없음 | “이메일 또는 비밀번호를 확인해 주세요”를 표시합니다. |
| `NETWORK_ERROR` | `SocketAuthClient` | 연결에 실패했거나 응답 전에 연결이 끊김 | 연결 실패를 안내합니다. 회원가입 화면에서는 로그인으로 가입 여부를 확인할 수 있게 안내합니다. |
| `REQUEST_TIMEOUT` | `SocketAuthClient` | 제한 시간 안에 응답을 받지 못함 | 서버 응답이 늦다는 안내를 표시합니다. 회원가입은 처리됐을 수 있으므로 로그인으로 가입 여부를 확인할 수 있게 안내합니다. |

화면은 빈칸이나 비밀번호 확인 불일치를 요청 전에 검사합니다. 요청을 보낸 동안에는 제출 버튼을 비활성화하고, 실패 응답이 오면 다시 활성화합니다. `MockAuthClient`도 같은 코드를 담아 반환하면 실제 서버 없이 실패 화면을 확인할 수 있습니다.

## Java 응답 예시

```java
var signUpSuccess = new SignUpResult(true, 42L, null);
var duplicateEmail = new SignUpResult(false, null, AuthErrorCode.EMAIL_ALREADY_EXISTS);
var loginSuccess = new LoginResult(true, 42L, "예시 회원", "example-session-token", null);
var invalidLogin = new LoginResult(false, null, null, null, AuthErrorCode.INVALID_CREDENTIALS);
```
