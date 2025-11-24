package guitool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SubInquiryPage extends JPanel {
    
    // 디자인 상수
    private static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    private static final Color COLOR_BUTTON = new Color(225, 205, 188);
    private static final Color COLOR_BUTTON_HOVER = new Color(218, 184, 153);
    private static final Color COLOR_TEXT = new Color(77, 77, 77);

    private JTextField inputField;
    private JButton searchButton;
    private JLabel label;

    public SubInquiryPage(String category) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30)); // 간격 조정
        setBackground(COLOR_BACKGROUND); // 배경색 설정

        label = new JLabel(category + " : ");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(COLOR_TEXT);

        inputField = new JTextField(15);
        inputField.setPreferredSize(new Dimension(150, 30)); // 입력창 크기

        // 버튼 생성 및 스타일링
        searchButton = new JButton("조회");
        styleButton(searchButton);

        add(label);
        add(inputField);
        add(searchButton);
    }

    public String getInputText() {
        return inputField.getText();
    }

    public void setSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    private void styleButton(JButton button) {
        button.setBackground(COLOR_BUTTON);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_BUTTON);
            }
        });
    }
}