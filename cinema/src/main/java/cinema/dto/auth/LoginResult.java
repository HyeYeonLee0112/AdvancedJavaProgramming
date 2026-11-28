package cinema.dto.auth;

/**
 * 로그인 처리 결과입니다. 성공하면 회원 정보와 세션 토큰이 채워지고
 * 오류 코드는 {@code null}입니다. 실패하면 오류 코드가 채워지고
 * 회원 정보와 세션 토큰은 {@code null}입니다.
 *
 * @param success 로그인 성공 여부
 * @param memberId 성공한 회원의 번호
 * @param displayName 성공한 회원의 표시 이름
 * @param sessionToken 로그인 이후 인증이 필요한 요청에 사용할 토큰
 * @param errorCode 실패 원인을 나타내는 코드. 예를 들어 {@link AuthErrorCode#INVALID_CREDENTIALS}
 */
public record LoginResult(
        boolean success,
        Long memberId,
        String displayName,
        String sessionToken,
        AuthErrorCode errorCode
) {
    @Override
    public String toString() {
        return "LoginResult[success=" + success + ", memberId=" + memberId + ", errorCode=" + errorCode + "]";
    }

    public LoginResult {
        if (success) {
            boolean missingRequiredValue = memberId == null || displayName == null || sessionToken == null;
            if (missingRequiredValue || memberId <= 0 || displayName.isBlank()
                    || sessionToken.isBlank() || errorCode != null) {
                throw new IllegalArgumentException("로그인 성공 응답에 필요한 값이 없습니다.");
            }
        } else if (memberId != null || displayName != null || sessionToken != null || errorCode == null) {
            throw new IllegalArgumentException("로그인 실패 응답에는 오류 코드만 있어야 합니다.");
        }
    }
}
