package guitool;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UITheme {

    // --- 색상 정의 ---
    public static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    public static final Color COLOR_BUTTON = new Color(225, 205, 188);
    public static final Color COLOR_BUTTON_HOVER = new Color(218, 184, 153);
    public static final Color COLOR_BUTTON_ACTIVE = new Color(200, 170, 140);
    public static final Color COLOR_TABLE_HEADER = new Color(218, 184, 153);
    public static final Color COLOR_TEXT = new Color(77, 77, 77);
    public static final Color COLOR_GRID = new Color(220, 200, 185);
    public static final Color COLOR_ROW_ALT = new Color(247, 241, 235);
    public static final Color COLOR_GUEST_BUTTON_LIGHT = new Color(180, 160, 140); // GuestLogin의 buttonColor
    public static final Color COLOR_BUTTON_SPECIAL_YELLOW = new Color(255, 250, 205); // InquiryPage의 연한 노랑
    public static final Color COLOR_BUTTON_SPECIAL_RED = new Color(255, 200, 200);   // InquiryPage의 연한 빨강
    public static final Color COLOR_TEXTAREA_BACKGROUND = new Color(250, 245, 235); // GuestLogin의 JTextArea 배경색

    // --- 폰트 정의 ---
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BUTTON_GUEST = new Font("SansSerif", Font.PLAIN, 13); // GuestLogin의 FONT_BUTTON
    public static final Font FONT_LABEL_GUEST = new Font("SansSerif", Font.PLAIN, 12); // GuestLogin의 FONT_LABEL
    public static final Font FONT_TABLE_HEADER = new Font("SansSerif", Font.BOLD, 12); // ShippingPage, InquiryPage 테이블 헤더용

    // --- 기타 UI 상수 ---
    public static final Border BUTTON_BORDER = new EmptyBorder(6, 12, 6, 12); // 기본 버튼 패딩
    public static final Border GUEST_BUTTON_BORDER = new EmptyBorder(8, 15, 8, 15); // GuestLogin 버튼 패딩
    public static final Dimension BUTTON_DIMENSION_MEDIUM = new Dimension(110, 30); // InquiryPage 버튼 기본 크기
    public static final Dimension BUTTON_DIMENSION_SMALL = new Dimension(100, 30);  // InquiryPage 버튼 조절된 크기

    // --- 공통 스타일링 메서드 ---

    // 기본 스타일이 적용된 JButton 생성
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BUTTON);
        button.setForeground(COLOR_TEXT);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BUTTON_BORDER);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 특정 활성화 버튼 로직은 호출하는 곳에서 처리하도록
                button.setBackground(COLOR_BUTTON);
            }
        });
        return button;
    }

    // GuestLogin 페이지용 버튼 스타일이 적용된 JButton을 생성합니다.
    public static JButton createGuestStyledButton(String text, Dimension buttonDim) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_GUEST_BUTTON_LIGHT);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(FONT_BUTTON_GUEST);
        button.setPreferredSize(buttonDim);
        // GuestLogin의 버튼은 별도의 mouseEntered/Exited 로직이 없었으므로 여기서는 추가하지 않음
        return button;
    }

    //표준 스타일이 적용된 JTable을 생성하여 반환합니다.
    public static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? UITheme.COLOR_BACKGROUND : UITheme.COLOR_ROW_ALT);
                    c.setForeground(UITheme.COLOR_TEXT);
                } else {
                    c.setBackground(UITheme.COLOR_BUTTON_HOVER);
                    c.setForeground(UITheme.COLOR_TEXT);
                }
                return c;
            }
        };

        table.setFont(UITheme.FONT_LABEL);
        table.setForeground(UITheme.COLOR_TEXT);
        table.setGridColor(UITheme.COLOR_GRID);
        table.setRowHeight(25); // 높이 25로 통일
        table.setSelectionBackground(UITheme.COLOR_BUTTON_HOVER);
        table.setSelectionForeground(UITheme.COLOR_TEXT);
        table.setBackground(UITheme.COLOR_BACKGROUND);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.COLOR_TABLE_HEADER);
        header.setForeground(UITheme.COLOR_TEXT);
        header.setFont(UITheme.FONT_TABLE_HEADER);
        header.setPreferredSize(new Dimension(100, 30));

        return table;
    }
}
