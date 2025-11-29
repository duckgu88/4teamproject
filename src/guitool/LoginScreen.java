package guitool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 사용자 ID와 비밀번호를 입력받아 관리자 로그인을 처리하거나, 게스트 로그인을 통해 배송 조회를 수행
public class LoginScreen extends JPanel {

    private Image backgroundImage; // 배경 이미지
    private MainFrame mainFrame; // 메인 프레임 (화면 전환용)

    // UI 컴포넌트들을 초기화하고 이벤트 리스너를 설정합니다.
    public LoginScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // 배경 이미지를 로드합니다.
        try {
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

        setLayout(null); // 절대 위치 지정을 위해 null 레이아웃 설정

        // --- 로그인 UI 컴포넌트 설정 ---
        int startX_label = 510;
        int startX_field = 580;
        int fieldWidth = 190;
        int labelWidth = 60;
        int startY = 190;
        int verticalGap = 40;
        int buttonGap = 35;

        // 아이디 라벨 및 입력 필드
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(startX_label, startY, labelWidth, 25);
        idLabel.setForeground(Color.BLACK);
        add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(startX_field, startY, fieldWidth, 25);
        add(idText);

        // 비밀번호 라벨 및 입력 필드
        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(startX_label, startY + verticalGap, labelWidth, 25);
        pwLabel.setForeground(Color.BLACK);
        add(pwLabel);

        JPasswordField pwText = new JPasswordField(20);
        pwText.setBounds(startX_field, startY + verticalGap, fieldWidth, 25);
        add(pwText);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(startX_field, startY + (verticalGap * 2), 90, 25);
        add(loginButton);

        // 취소 버튼
        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(startX_field + 90 + 10, startY + (verticalGap * 2), 90, 25);
        add(cancelButton);

        // 게스트 로그인 버튼
        JButton guestLoginButton = new JButton("게스트 로그인");
        guestLoginButton.setBounds(startX_field, startY + (verticalGap * 2) + buttonGap, fieldWidth, 25);
        add(guestLoginButton);

        // --- 이벤트 리스너 설정 ---
        // 로그인 버튼 액션: 관리자 계정 확인 후 ShippingPage로 전환
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String password = new String(pwText.getPassword());

                if (id.equals("manager") && password.equals("qwer1234")) {
                    if (mainFrame == null) { 
                        JOptionPane.showMessageDialog(LoginScreen.this, "애플리케이션이 올바르게 초기화되지 않았습니다. GUIApp.main()을 통해 실행해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainFrame.showPage("SHIPPING"); // 배송 관리 페이지로 전환
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 게스트 로그인 버튼 액션: GuestLogin JDialog를 생성하여 표시
        guestLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // MainFrame을 부모로 하는 GuestLogin JDialog 생성 (모달로 동작)
                GuestLogin guestDialog = new GuestLogin(mainFrame); 
                guestDialog.setVisible(true); // 다이얼로그 표시
            }
        });

        // 취소 버튼 액션: 애플리케이션 종료
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 프로그램 종료
            }
        });
    }

    // 배경 이미지
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 배경 이미지를 패널 크기에 맞춤
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 이미지가 없을 경우 기본 회색 배경으로 설정
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // 패널을 독립적으로 테스트하기 위한 main 메소드 삭제 가능
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame testFrame = new JFrame("Login Screen Test");
            testFrame.add(new LoginScreen(null)); 
            testFrame.setSize(800, 500);
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setLocationRelativeTo(null);
            testFrame.setVisible(true);
        });
    }
}