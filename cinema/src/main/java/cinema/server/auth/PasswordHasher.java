package cinema.server.auth;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordHasher {
    // 이 클래스에서 공통으로 쓰는, 값이 고정된 PBKDF2 반복 횟수입니다.
    static final int ITERATIONS = 600_000;
    private final SecureRandom random = new SecureRandom();

    // 가입마다 무작위 바이트 16개를 새로 준비합니다.
    byte[] newSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    byte[] derive(String password, byte[] salt, int iterations)
            throws GeneralSecurityException {
        // PBEKeySpec이 받을 문자 배열을 만듭니다. 256은 결과의 비트 길이입니다.
        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 256);
        // 지정한 알고리즘으로 계산한 키를 바이트 배열로 돌려줍니다.
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        // 계산의 성공 여부와 관계없이 작업에 사용한 비밀번호 문자 배열을 지웁니다.
        } finally {
            spec.clearPassword();
            Arrays.fill(chars, '\0');
        }
    }
}