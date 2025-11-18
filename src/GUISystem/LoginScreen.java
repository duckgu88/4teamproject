package GUISystem;

import javax.swing.*;
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.io.File; 

// 메인 클래스 (로그인 창)
public class LoginScreen extends JFrame {

    private Image backgroundImage; 

    public LoginScreen() {
        // --- 1. 배경 이미지 로드 ---
        try {
            // ★★★ 이미지 경로는 리소스 폴더 기준으로 변경 권장 ★★★
            // backgroundImage = new ImageIcon("4teamproject-main\\src\\image\\backgroundimage.jpg").getImage();
            // ↓↓↓ 이렇게 변경하는 것을 추천합니다 ↓↓↓
            backgroundImage = new ImageIcon(getClass().getResource("/image/backgroundimage.jpg")).getImage();
        } catch (Exception e) {
            System.err.println("배경 이미지 로드 실패: " + e.getMessage());
            backgroundImage = null; 
        }

        // 프레임(창) 설정
        setTitle("로그인 화면");
        setSize(800, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false); 

        // --- 2. 배경 이미지를 그릴 커스텀 패널 생성 ---
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

        // --- 3. 로그인 컴포넌트들 ---
        int startX_label = 510; 
        int startX_field = 580; 
        int fieldWidth = 190; 
        int labelWidth = 60; 
        int startY = 190; 
        int verticalGap = 40; 
        
        // 아이디
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(startX_label, startY, labelWidth, 25); 
        idLabel.setForeground(Color.BLACK); 
        backgroundPanel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(startX_field, startY, fieldWidth, 25); 
        backgroundPanel.add(idText);

        // 비밀번호
        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(startX_label, startY + verticalGap, labelWidth, 25); 
        pwLabel.setForeground(Color.BLACK); 
        backgroundPanel.add(pwLabel);

        JPasswordField pwText = new JPasswordField(20);
        pwText.setBounds(startX_field, startY + verticalGap, fieldWidth, 25); 
        backgroundPanel.add(pwText);

        // 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(startX_field, startY + (verticalGap * 2), 90, 25); 
        backgroundPanel.add(loginButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(startX_field + 90 + 10, startY + (verticalGap * 2), 90, 25); 
        backgroundPanel.add(cancelButton);

        // --- 이벤트 리스너 ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String password = new String(pwText.getPassword());

                if (id.equals("manager") && password.equals("qwer1234")) { 
                    // ★★★ 이름 변경됨 ★★★
                    new MainMenu(); 
                    dispose(); // 로그인 창 닫기
                } else {
                    JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                }
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

    // --- 프로그램 시작점 ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen();
            }
        });
    }
}