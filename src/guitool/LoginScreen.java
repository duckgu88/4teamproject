package guitool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private Image backgroundImage;

    public LoginScreen() {
        // --- 1. 배경 이미지 로드 ---
        try {
            // 경로 수정: /guitool/image/...
            java.net.URL imgUrl = getClass().getResource("/image/backgroundimage.jpg");
            
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("이미지 경로를 찾을 수 없습니다: /image/backgroundimage.jpg");
            }
        } catch (Exception e) {
            System.err.println("배경 이미지 로드 실패: " + e.getMessage());
            backgroundImage = null;
        }

        setTitle("로그인 화면");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- 2. 배경 패널 ---
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // --- 3. 로그인 UI ---
        int startX_label = 510;
        int startX_field = 580;
        int fieldWidth = 190;
        int labelWidth = 60;
        int startY = 190;
        int verticalGap = 40;
        int buttonGap = 35;

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(startX_label, startY, labelWidth, 25);
        idLabel.setForeground(Color.BLACK);
        backgroundPanel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(startX_field, startY, fieldWidth, 25);
        backgroundPanel.add(idText);

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(startX_label, startY + verticalGap, labelWidth, 25);
        pwLabel.setForeground(Color.BLACK);
        backgroundPanel.add(pwLabel);

        JPasswordField pwText = new JPasswordField(20);
        pwText.setBounds(startX_field, startY + verticalGap, fieldWidth, 25);
        backgroundPanel.add(pwText);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(startX_field, startY + (verticalGap * 2), 90, 25);
        backgroundPanel.add(loginButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(startX_field + 90 + 10, startY + (verticalGap * 2), 90, 25);
        backgroundPanel.add(cancelButton);

        JButton guestLoginButton = new JButton("게스트 로그인");
        guestLoginButton.setBounds(startX_field, startY + (verticalGap * 2) + buttonGap, fieldWidth, 25);
        backgroundPanel.add(guestLoginButton);

        // --- 이벤트 리스너 ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String password = new String(pwText.getPassword());

                if (id.equals("manager") && password.equals("qwer1234")) {
                    new MainMenu();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 게스트 로그인 -> GuestLogin 창 열기
        guestLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GuestLogin(LoginScreen.this); // 현재 창(this)을 다음 창으로 넘겨줌
                setVisible(false); // 로그인 창은 숨김
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}