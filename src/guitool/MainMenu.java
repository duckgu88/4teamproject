package guitool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    
    public MainMenu() {
        setTitle("메인 메뉴"); 
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 80)); 

        // 배경색 설정 
        getContentPane().setBackground(new Color(239, 222, 207)); 

        // --- 1. 아이콘 로드 ---
        // ★★★ 이미지 경로는 리소스 폴더 기준으로 변경 권장 ★★★
        // ImageIcon shippingIcon = new ImageIcon("4teamproject-main\\src\\image\\deliever.png");
        // ImageIcon inquiryIcon = new ImageIcon("4teamproject-main\\src\\image\\lookup.png");
        // ↓↓↓ 이렇게 변경하는 것을 추천합니다 ↓↓↓
        ImageIcon shippingIcon = new ImageIcon(getClass().getResource("/image/deliever.png"));
        ImageIcon inquiryIcon = new ImageIcon(getClass().getResource("/image/lookup.png"));

        // 이미지 크기 조절 (예: 50x50) - 아이콘이 너무 크거나 작을 경우 조절합니다.
        Image scaledShippingImg = shippingIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        Image scaledInquiryImg = inquiryIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        
        ImageIcon finalShippingIcon = new ImageIcon(scaledShippingImg);
        ImageIcon finalInquiryIcon = new ImageIcon(scaledInquiryImg);

        // --- 2. "배송" 버튼 (아이콘 포함) ---
        JButton shippingButton = new JButton("배송", finalShippingIcon);
        shippingButton.setPreferredSize(new Dimension(120, 80)); // 버튼 크기 설정
        shippingButton.setVerticalTextPosition(SwingConstants.BOTTOM);   // 텍스트를 아이콘 아래에
        shippingButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트를 아이콘 중앙에
        shippingButton.setFocusPainted(false); // 포커스 테두리 제거 (원하는 경우)
        
        // --- 3. "조회" 버튼 (아이콘 포함) ---
        JButton inquiryButton = new JButton("조회", finalInquiryIcon);
        inquiryButton.setPreferredSize(new Dimension(120, 80)); // 버튼 크기 설정
        inquiryButton.setVerticalTextPosition(SwingConstants.BOTTOM);   // 텍스트를 아이콘 아래에
        inquiryButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트를 아이콘 중앙에
        inquiryButton.setFocusPainted(false); // 포커스 테두리 제거 (원하는 경우)

        // "배송" 버튼 리스너
        shippingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShippingPage(MainMenu.this);
                setVisible(false);
            }
        });

        // "조회" 버튼 리스너
        inquiryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InquiryPage(MainMenu.this); // 조회 페이지 열기
                setVisible(false);
            }
        });

        // 프레임에 버튼 추가
        add(shippingButton);
        add(inquiryButton);

        setVisible(true);
    }
}