package cinema.dto.auth;

/**
 * 이메일과 비밀번호로 로그인을 요청할 때 화면이 전송하는 값입니다.
 *
 * @param email 로그인할 계정의 이메일
 * @param password 사용자가 입력한 비밀번호
 */
public record LoginRequest(String email, String password) {
    @Override
    public String toString() {
        return "LoginRequest[credentials=<redacted>]";
    }
}
