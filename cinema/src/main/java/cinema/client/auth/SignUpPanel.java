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
import cinema.dto.auth.SignUpRequest;
import cinema.dto.auth.SignUpResult;

/**
 * 가입 정보를 확인하고, 가입이 끝나면 로그인 화면으로 돌아갑니다.
 */
public final class SignUpPanel extends JPanel {
    private final AuthClient authClient;
    private final AuthPanel screens;

    private final JTextField name = new JTextField(26);
    private final JTextField email = new JTextField(26);
    private final JPasswordField password = new JPasswordField(26);
    private final JPasswordField passwordConfirm = new JPasswordField(26);
    private final JButton submitButton = new JButton("가입하기");
    private final JButton backButton = new JButton("로그인으로 돌아가기");
    private final JTextArea message = new JTextArea(3, 26);

    // 가입 폼을 만들고 제출과 로그인 화면으로 돌아가기 동작을 연결합니다.
    SignUpPanel(AuthClient authClient, AuthPanel screens) {
        super(new BorderLayout());
        this.authClient = authClient;
        this.screens = screens;

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel("회원가입");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        form.add(title);
        form.add(Box.createVerticalStrut(12));
        addField(form, "표시 이름", name);
        addField(form, "이메일", email);
        addField(form, "비밀번호", password);
        addField(form, "비밀번호 확인", passwordConfirm);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(UIManager.getFont("Label.font"));
        message.setAlignmentX(LEFT_ALIGNMENT);
        message.getAccessibleContext().setAccessibleName("회원가입 안내");
        form.add(message);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 8, 0));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.add(backButton);
        buttons.add(submitButton);
        form.add(buttons);
        add(form, BorderLayout.NORTH);

        submitButton.addActionListener(event -> submit());
        passwordConfirm.addActionListener(event -> submit());
        backButton.addActionListener(event -> {
            clearPasswords();
            message.setText("");
            screens.showLogin();
        });
    }

    // 입력값을 확인한 뒤 서버에 보낼 회원가입 요청을 만듭니다.
    private void submit() {
        if (!submitButton.isEnabled()) {
            return;
        }

        String enteredName = name.getText().strip();
        String address = email.getText().strip();
        char[] passwordChars = password.getPassword();
        char[] confirmChars = passwordConfirm.getPassword();
        String enteredPassword = new String(passwordChars);
        boolean passwordMatches = Arrays.equals(passwordChars, confirmChars);
        Arrays.fill(passwordChars, '\0');
        Arrays.fill(confirmChars, '\0');

        // 빈칸과 비밀번호 확인은 화면에서 먼저 검사합니다.
        if (enteredName.isEmpty() || address.isEmpty() || enteredPassword.isEmpty()) {
            message.setText("표시 이름, 이메일, 비밀번호를 모두 입력해 주세요.");
            return;
        }
        if (!passwordMatches) {
            message.setText("비밀번호와 비밀번호 확인이 일치하지 않아요.");
            passwordConfirm.requestFocusInWindow();
            return;
        }

        SignUpRequest request = new SignUpRequest(address, enteredPassword, enteredName);
        setBusy(true);
        message.setText("가입을 처리하고 있어요.");

        new SwingWorker<SignUpResult, Void>() {
            // 가입 요청은 화면 밖에서 처리해 응답을 기다리는 동안에도 창이 멈추지 않게 합니다.
            @Override
            protected SignUpResult doInBackground() {
                return authClient.signUp(request);
            }

            // 요청이 끝나면 가입 결과를 화면에 반영하고 다시 제출할 수 있게 합니다.
            @Override
            protected void done() {
                setBusy(false);
                try {
                    showResult(get(), request.email());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    message.setText("회원가입 요청이 중단됐어요.");
                } catch (ExecutionException exception) {
                    message.setText("회원가입 요청을 처리할 수 없어요.");
                }
            }
        }.execute();
    }

    // 성공하면 로그인 화면으로 돌아가고, 실패하면 받은 오류 코드에 맞춰 안내합니다.
    private void showResult(SignUpResult result, String registeredEmail) {
        if (result.success()) {
            name.setText("");
            email.setText("");
            clearPasswords();
            message.setText("");
            screens.showLoginAfterSignUp(registeredEmail);
            return;
        }

        // 응답이 끊겼다면 가입이 이미 처리됐을 수도 있어 로그인으로 확인을 안내합니다.
        message.setText(switch (result.errorCode()) {
            case EMAIL_ALREADY_EXISTS -> "이미 가입된 이메일이에요.";
            case NETWORK_ERROR -> "서버에 연결할 수 없어요. 로그인으로 가입 여부를 확인해 주세요.";
            case REQUEST_TIMEOUT -> "서버 응답이 늦어지고 있어요. 로그인으로 가입 여부를 확인해 주세요.";
            case null, default -> "회원가입 요청을 처리할 수 없어요.";
        });
        if (result.errorCode() == AuthErrorCode.EMAIL_ALREADY_EXISTS) {
            email.requestFocusInWindow();
        }
    }

    // 화면을 떠날 때 입력한 비밀번호 두 칸을 비웁니다.
    private void clearPasswords() {
        password.setText("");
        passwordConfirm.setText("");
    }

    // 가입 응답을 기다리는 동안 내용을 고치거나 같은 요청을 중복해서 보내지 못하게 합니다.
    private void setBusy(boolean busy) {
        submitButton.setEnabled(!busy);
        backButton.setEnabled(!busy);
        name.setEditable(!busy);
        email.setEditable(!busy);
        password.setEditable(!busy);
        passwordConfirm.setEditable(!busy);
    }

    // 입력칸의 이름과 값을 쓰는 칸을 함께 폼에 추가합니다.
    private void addField(JPanel form, String title, JTextField field) {
        JLabel label = new JLabel(title);
        label.setLabelFor(field);
        field.setAlignmentX(LEFT_ALIGNMENT);
        form.add(label);
        form.add(field);
        form.add(Box.createVerticalStrut(8));
    }
}
