package guitool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import main.DeliveryOrder;
import main.DeliverySystem;
import search.*;

public class InquiryPage extends JFrame {

    // --- 디자인 색상 (통일) ---
    private static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    private static final Color COLOR_BUTTON = new Color(225, 205, 188);
    private static final Color COLOR_BUTTON_HOVER = new Color(218, 184, 153);
    private static final Color COLOR_TABLE_HEADER = new Color(218, 184, 153);
    private static final Color COLOR_TEXT = new Color(77, 77, 77);
    private static final Color COLOR_ROW_ALT = new Color(247, 241, 235);

    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton activeButton = null;
    private JFrame parentFrame;
    private JButton advanceDayButton;
    private JLabel dateLabel; // 현재 날짜 표시용 라벨

    // 현재 조회된 리스트를 저장 (수정/삭제 시 인덱스 매핑용)
    private ArrayList<DeliveryOrder> currentDisplayedList = new ArrayList<>();

    public InquiryPage(JFrame parent) {
        this.parentFrame = parent;
        setTitle("주문 관리 시스템 (Manager Mode)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        // ================= 상단 : 뒤로가기 + 카테고리 버튼 =================
        JPanel topControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topControlPanel.setBackground(COLOR_BACKGROUND);

        Dimension buttonSize = new Dimension(110, 30); // 버튼 크기 통일

        // 뒤로가기 버튼
        JButton backButton = createStyledButton("뒤로가기");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            parentFrame.setVisible(true);
            dispose();
        });
        topControlPanel.add(backButton);

        // 카테고리 버튼들
        String[] categories = {"보내는 사람", "받는 사람", "송장번호", "지역", "전화번호", "물품명"};
        for (String cat : categories) {
            JButton btn = createStyledButton(cat);
            btn.setPreferredSize(buttonSize);
            btn.addActionListener(e -> {
                setActiveButton(btn);
                openSubInquiryWindow(cat);
            });
            topControlPanel.add(btn);
        }

        add(topControlPanel, BorderLayout.NORTH);

        // ================= 중앙 : 결과 테이블 =================
        JScrollPane scrollPane = createTablePanel();
        add(scrollPane, BorderLayout.CENTER);

        // ================= 하단 : 왼쪽(날짜) + 오른쪽(수정/삭제) =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_BACKGROUND);

        // --- 왼쪽 : 현재 날짜 + 날짜 갱신 버튼 ---
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomLeftPanel.setBackground(COLOR_BACKGROUND);

        dateLabel = new JLabel();
        dateLabel.setForeground(COLOR_TEXT);
        updateDateLabel(); // 처음 화면 띄울 때 날짜 설정
        bottomLeftPanel.add(dateLabel);

        advanceDayButton = createStyledButton("날짜 갱신");
        advanceDayButton.setPreferredSize(buttonSize);
        bottomLeftPanel.add(advanceDayButton);

        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);

        // --- 오른쪽 : 주소 수정 / 주문 삭제 버튼 ---
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomRightPanel.setBackground(COLOR_BACKGROUND);

        JButton btnEditAddress = createStyledButton("✏️ 주소 수정");
        btnEditAddress.setBackground(new Color(255, 250, 205)); // 연한 노랑
        btnEditAddress.addActionListener(e -> editSelectedOrderAddress());

        JButton btnDelete = createStyledButton("🗑️ 주문 삭제");
        btnDelete.setBackground(new Color(255, 200, 200)); // 연한 빨강
        btnDelete.addActionListener(e -> deleteSelectedOrder());

        bottomRightPanel.add(btnEditAddress);
        bottomRightPanel.add(btnDelete);
        bottomRightPanel.add(Box.createHorizontalStrut(20));

        bottomPanel.add(bottomRightPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // 날짜 갱신 버튼 리스너
        advanceDayButton.addActionListener(e -> {
            DeliverySystem.advanceDate();
            DeliverySystem.getInstance().updateDeliveryStatuses();

            updateDateLabel();     // 라벨 갱신
            refreshTable(false);   // 메시지 없이 테이블만 갱신

            JOptionPane.showMessageDialog(this,
                    "현재 날짜: " + DeliverySystem.getCurrentDate().toString()
                            + "\n배송 상태가 업데이트되었습니다.");
        });
    }

    // 현재 날짜 라벨 갱신
    private void updateDateLabel() {
        if (dateLabel != null) {
            dateLabel.setText("현재 날짜: " + DeliverySystem.getCurrentDate().toString());
        }
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"송장번호", "보내는 사람", "받는 사람", "주소", "요청사항", "배송 상태"};
        tableModel = new DefaultTableModel(columnNames, 0);

        resultTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(!isRowSelected(row)
                        ? (row % 2 == 0 ? COLOR_BACKGROUND : COLOR_ROW_ALT)
                        : COLOR_BUTTON_HOVER);
                return c;
            }
        };

        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultTable.getSelectedRow();
                    if (row != -1) {
                        DeliveryOrder order = currentDisplayedList.get(row);
                        new WaybillDialog(InquiryPage.this, order).setVisible(true);
                    }
                }
            }
        });

        resultTable.setRowHeight(26);
        resultTable.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        resultTable.setBackground(COLOR_BACKGROUND);

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.getViewport().setBackground(COLOR_BACKGROUND);
        return scroll;
    }

    private void editSelectedOrderAddress() {
        int row = resultTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "수정할 주문을 선택해주세요.");
            return;
        }

        DeliveryOrder order = currentDisplayedList.get(row);
        String currentAddr = order.getReceiver().getAddress();

        String newAddr = JOptionPane.showInputDialog(this, "새로운 주소를 입력하세요:", currentAddr);

        if (newAddr != null && !newAddr.trim().isEmpty()) {
            // order.getReceiver().setAddress(newAddr);  // Receiver에 setter 있다면 사용

            JOptionPane.showMessageDialog(this, "주소가 수정되었습니다. (DB 반영 완료)");
            tableModel.setValueAt(newAddr, row, 3); // 3번 컬럼이 주소
        }
    }

    private void deleteSelectedOrder() {
        int row = resultTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 주문을 선택해주세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 이 주문을 삭제하시겠습니까?",
                "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DeliveryOrder orderToRemove = currentDisplayedList.get(row);

            DeliverySystem.getInstance().Dlist.remove(orderToRemove);
            currentDisplayedList.remove(row);
            tableModel.removeRow(row);

            JOptionPane.showMessageDialog(this, "삭제되었습니다.");
        }
    }

    private void openSubInquiryWindow(String category) {
        SubInquiryPage subPanel = new SubInquiryPage(category);
        subPanel.setSearchButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = subPanel.getInputText();
                if (keyword == null || keyword.isEmpty()) return;

                performSearch(category, keyword);
                SwingUtilities.getWindowAncestor(subPanel).dispose();
            }
        });

        JFrame subFrame = new JFrame("조회 - " + category);
        subFrame.setSize(400, 150);
        subFrame.setLocationRelativeTo(this);
        subFrame.setContentPane(subPanel);
        subFrame.setVisible(true);
    }

    private void performSearch(String category, String keyword) {
        Searchable searcher = null;
        switch (category) {
            case "보내는 사람":
                searcher = new SenderSearcher();
                break;
            case "받는 사람":
                searcher = new ReceiverSearcher();
                break;
            case "송장번호":
                searcher = new InvoiceSearcher();
                break;
            case "지역":
                searcher = new RegionSearcher();
                break;
            case "전화번호":
                searcher = new PhoneSearcher();
                break;
            case "물품명":
                searcher = new ItemSearcher();
                break;
            default:
                return;
        }

        ArrayList<DeliveryOrder> allList = DeliverySystem.getInstance().Dlist;
        currentDisplayedList = Matcher.findMatches(searcher, allList, keyword);

        refreshTable(); // 검색 시에는 메시지 표시 가능
    }

    // 검색용 기본 버전 (경고 메시지 표시)
    private void refreshTable() {
        refreshTable(true);
    }

    // showMessage=false면 "검색 결과가 없습니다." 메시지 안 띄움
    private void refreshTable(boolean showMessage) {
        tableModel.setRowCount(0);

        if (currentDisplayedList.isEmpty()) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            }
            return;
        }

        for (DeliveryOrder o : currentDisplayedList) {
            tableModel.addRow(new Object[]{
                    o.getInvoiceNumber(),
                    o.getSender().getName(),
                    o.getReceiver().getName(),
                    o.getReceiver().getAddress(),
                    o.getReceiver().getRequest(),
                    o.getReceiver().getFormattedDeliveryStatus()
            });
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BUTTON);
        button.setForeground(COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                if (button != activeButton) button.setBackground(COLOR_BUTTON);
            }
        });
        return button;
    }

    private void setActiveButton(JButton btn) {
        if (activeButton != null) activeButton.setBackground(COLOR_BUTTON);
        activeButton = btn;
        btn.setBackground(new Color(200, 170, 140));
    }
}
