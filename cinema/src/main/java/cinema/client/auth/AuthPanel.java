package cinema.client.auth;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import cinema.client.transport.AuthClient;
import cinema.dto.auth.LoginResult;

/**
 * 로그인과 회원가입 중 어떤 화면을 보여 줄지 관리합니다.
 */
public final class AuthPanel extends JPanel {
    private static final String LOGIN = "login";
    private static final String SIGN_UP = "signUp";

    private final CardLayout layout = new CardLayout();
    private final JPanel cards = new JPanel(layout);
    private final LoginPanel loginPanel;

    // 창이 넘겨준 동작입니다. 로그인에 성공했을 때 회원 정보를 알려 주는 데 씁니다.
    private final Consumer<LoginResult> onLoginSuccess;

    // 두 화면을 만들고 로그인 화면부터 보여 줍니다.
    public AuthPanel(AuthClient authClient, Consumer<LoginResult> onLoginSuccess) {
        super(new GridBagLayout());
        Objects.requireNonNull(authClient, "authClient");
        this.onLoginSuccess = Objects.requireNonNull(onLoginSuccess, "onLoginSuccess");
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        loginPanel = new LoginPanel(authClient, this);
        cards.add(loginPanel, LOGIN);
        cards.add(new SignUpPanel(authClient, this), SIGN_UP);
        add(cards);
        showLogin();
    }

    // 이미 만들어 둔 로그인 화면을 앞으로 가져옵니다.
    void showLogin() {
        layout.show(cards, LOGIN);
    }

    // 회원가입 버튼을 눌렀을 때 가입 화면을 보여 줍니다.
    void showSignUp() {
        layout.show(cards, SIGN_UP);
    }

    // 가입이 끝나면 로그인 화면으로 돌아가 방금 가입한 이메일을 채웁니다.
    void showLoginAfterSignUp(String email) {
        showLogin();
        loginPanel.showSignUpSuccess(email);
    }

    // 로그인 결과를 창에 넘겨 다음 화면을 열 수 있게 합니다.
    void finishLogin(LoginResult result) {
        onLoginSuccess.accept(result);
    }
}
