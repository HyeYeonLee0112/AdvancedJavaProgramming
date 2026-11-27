package cinema.dto.auth;

/**
 * 새 계정을 만들 때 화면이 전송하는 값입니다.
 * 화면에서는 두 비밀번호 입력이 일치하는지 확인한 뒤 요청을 보냅니다.
 *
 * @param email 새 계정에 사용할 이메일
 * @param password 새 계정에 사용할 비밀번호
 * @param displayName 화면에 표시할 회원 이름
 */
public record SignUpRequest(String email, String password, String displayName) {
    @Override
    public String toString() {
        return "SignUpRequest[accountDetails=<redacted>]";
    }
}
