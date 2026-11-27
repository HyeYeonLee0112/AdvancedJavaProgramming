package cinema.dto.auth;

/**
 * 회원가입 처리 결과입니다. 성공하면 회원 번호가 채워지고 오류 코드는
 * {@code null}입니다. 실패하면 회원 번호는 {@code null}이고 오류 코드가 채워집니다.
 *
 * @param success 회원가입 성공 여부
 * @param memberId 성공 시 생성된 회원 번호
 * @param errorCode 실패 원인을 나타내는 코드. 예를 들어 {@link AuthErrorCode#EMAIL_ALREADY_EXISTS}
 */
public record SignUpResult(boolean success, Long memberId, AuthErrorCode errorCode) {
}
