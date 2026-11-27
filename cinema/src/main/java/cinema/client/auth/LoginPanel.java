package cinema.client.auth;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import cinema.client.transport.AuthClient;
import cinema.dto.auth.AuthErrorCode;
import cinema.dto.auth.LoginRequest;
import cinema.dto.auth.LoginResult;

/**
 * 이메일과 비밀번호를 확인하고 로그인 결과를 보여 줍니다.
 */
public final class LoginPanel extends JPanel {
    private final AuthClient authClient;
    private final AuthPanel screens;

    private final JTextField email = new JTextField(26);
    private final JPasswordField password = new JPasswordField(26);
    private final JButton loginButton = new JButton("로그인");
    private final JButton signUpButton = new JButton("회원가입");
    private final JTextArea message = new JTextArea(3, 26);

    // 로그인 폼을 만들고 버튼과 Enter 키에 어떤 동작을 할지 연결합니다.
    LoginPanel(AuthClient authClient, AuthPanel screens) {
        super(new BorderLayout());
        this.authClient = authClient;
        this.screens = screens;

        // 위에서부터 제목, 입력칸, 안내 문구, 버튼을 쌓습니다.
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel("로그인");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        form.add(title);
        form.add(Box.createVerticalStrut(12));
        addField(form, "이메일", email);
        addField(form, "비밀번호", password);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(UIManager.getFont("Label.font"));
        message.setAlignmentX(LEFT_ALIGNMENT);
        message.getAccessibleContext().setAccessibleName("로그인 안내");
        form.add(message);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 8, 0));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.add(signUpButton);
        buttons.add(loginButton);
        form.add(buttons);
        add(form, BorderLayout.NORTH);

        loginButton.addActionListener(event -> submit());
        password.addActionListener(event -> submit());
        signUpButton.addActionListener(event -> {
            password.setText("");
            message.setText("");
            screens.showSignUp();
        });
    }

    // 가입 성공 안내를 보여 주고 비밀번호만 입력하면 로그인할 수 있게 합니다.
    void showSignUpSuccess(String registeredEmail) {
        email.setText(registeredEmail);
        password.setText("");
        message.setText("가입이 완료됐어요. 비밀번호를 입력해 로그인해 주세요.");
        password.requestFocusInWindow();
    }

    // 입력칸이 비어 있는지 확인하고, 값이 있으면 로그인 요청을 시작합니다.
    private void submit() {
        if (!loginButton.isEnabled()) {
            return;
        }

        String address = email.getText().strip();
        if (address.isEmpty()) {
            message.setText("이메일을 입력해 주세요.");
            email.requestFocusInWindow();
            return;
        }

        char[] passwordChars = password.getPassword();
        if (passwordChars.length == 0) {
            message.setText("비밀번호를 입력해 주세요.");
            password.requestFocusInWindow();
            return;
        }

        LoginRequest request = new LoginRequest(address, new String(passwordChars));
        Arrays.fill(passwordChars, '\0');
        setBusy(true);
        message.setText("로그인 중이에요.");

        new SwingWorker<LoginResult, Void>() {
            // 로그인 요청은 화면 밖에서 처리해 서버를 기다리는 동안에도 창이 멈추지 않게 합니다.
            @Override
            protected LoginResult doInBackground() {
                return authClient.login(request);
            }

            // 요청이 끝난 뒤 화면에 결과를 반영하고 버튼과 입력칸을 다시 사용할 수 있게 합니다.
            @Override
            protected void done() {
                setBusy(false);
                try {
                    showResult(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    message.setText("로그인 요청이 중단됐어요.");
                } catch (ExecutionException exception) {
                    message.setText("로그인 요청을 처리할 수 없어요.");
                }
            }
        }.execute();
    }

    // 성공 결과는 창에 알리고, 실패하면 로그인 화면에 이유를 보여 줍니다.
    private void showResult(LoginResult result) {
        if (result.success()) {
            password.setText("");
            message.setText("");
            screens.finishLogin(result);
            return;
        }

        message.setText(switch (result.errorCode()) {
            case INVALID_CREDENTIALS -> "이메일 또는 비밀번호를 확인해 주세요.";
            case NETWORK_ERROR -> "서버에 연결할 수 없어요.";
            case REQUEST_TIMEOUT -> "서버 응답이 늦어지고 있어요.";
            case null, default -> "로그인 요청을 처리할 수 없어요.";
        });
        if (result.errorCode() == AuthErrorCode.INVALID_CREDENTIALS) {
            password.requestFocusInWindow();
        }
    }

    // 요청이 진행 중일 때 중복 제출을 막고, 끝나면 다시 입력할 수 있게 합니다.
    private void setBusy(boolean busy) {
        loginButton.setEnabled(!busy);
        signUpButton.setEnabled(!busy);
        email.setEditable(!busy);
        password.setEditable(!busy);
    }

    // 입력칸에 이름을 붙여 폼에 추가합니다.
    private void addField(JPanel form, String title, JTextField field) {
        JLabel label = new JLabel(title);
        label.setLabelFor(field);
        field.setAlignmentX(LEFT_ALIGNMENT);
        form.add(label);
        form.add(field);
        form.add(Box.createVerticalStrut(8));
    }
}
