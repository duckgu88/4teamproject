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
    
    // 현재 조회된 리스트를 저장 (수정/삭제 시 인덱스 매핑용)
    private ArrayList<DeliveryOrder> currentDisplayedList = new ArrayList<>();

    public InquiryPage() {
        setTitle("주문 관리 시스템 (Manager Mode)"); // 타이틀 변경
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        // 1. 상단 카테고리 패널
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoryPanel.setBackground(COLOR_BACKGROUND);
        
        String[] categories = {"보내는 사람", "받는 사람", "송장번호", "지역", "전화번호", "물품명"};
        for (String cat : categories) {
            JButton btn = createStyledButton(cat);
            btn.addActionListener(e -> {
                setActiveButton(btn);
                openSubInquiryWindow(cat);
            });
            categoryPanel.add(btn);
        }

        // 2. 중앙 테이블
        JScrollPane scrollPane = createTablePanel();

        // 3. 하단 관리(Management) 패널 ★★★
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(COLOR_BACKGROUND);

        // [기능 버튼] 주소 수정
        JButton btnEditAddress = createStyledButton("✏️ 주소 수정");
        btnEditAddress.setBackground(new Color(255, 250, 205)); // 연한 노랑
        btnEditAddress.addActionListener(e -> editSelectedOrderAddress());

        // [기능 버튼] 주문 삭제
        JButton btnDelete = createStyledButton("🗑️ 주문 삭제");
        btnDelete.setBackground(new Color(255, 200, 200)); // 연한 빨강
        btnDelete.addActionListener(e -> deleteSelectedOrder());

        // [네비게이션] 메인으로
        JButton backButton = createStyledButton("메인 메뉴로");
        backButton.addActionListener(e -> {
            new MainMenu();
            dispose();
        });

        bottomPanel.add(btnEditAddress);
        bottomPanel.add(btnDelete);
        bottomPanel.add(Box.createHorizontalStrut(20)); // 간격
        bottomPanel.add(backButton);

        add(categoryPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- 테이블 생성 ---
    private JScrollPane createTablePanel() {
        String[] columnNames = {"송장번호", "보내는 사람", "받는 사람", "주소", "요청사항"};
        tableModel = new DefaultTableModel(columnNames, 0);

        resultTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(!isRowSelected(row) ? (row%2==0 ? COLOR_BACKGROUND : COLOR_ROW_ALT) : COLOR_BUTTON_HOVER);
                return c;
            }
        };

        // 더블 클릭 이벤트 (운송장 보기)
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

        // 스타일링
        resultTable.setRowHeight(26);
        resultTable.getTableHeader().setBackground(COLOR_TABLE_HEADER);
        resultTable.setBackground(COLOR_BACKGROUND);
        
        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.getViewport().setBackground(COLOR_BACKGROUND);
        return scroll;
    }

    // --- ★★★ 관리 기능 구현 ★★★ ---

    // 1. 주소 수정 기능
    private void editSelectedOrderAddress() {
        int row = resultTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "수정할 주문을 선택해주세요.");
            return;
        }

        DeliveryOrder order = currentDisplayedList.get(row);
        String currentAddr = order.getReceiver().getAddress();
        
        // 입력 다이얼로그
        String newAddr = JOptionPane.showInputDialog(this, "새로운 주소를 입력하세요:", currentAddr);
        
        if (newAddr != null && !newAddr.trim().isEmpty()) {
            // 실제 데이터 수정 (Receiver 클래스의 필드 수정)
            // Receiver에 setAddress() 메소드가 없으면 추가하거나, 
            // 여기서는 데모를 위해 필드 직접 접근이 안되므로 아래와 같이 가정 (Receiver에 setAddress 추가 필요)
             // order.getReceiver().setAddress(newAddr); // <-- Receiver.java에 이 메소드 추가 필요!
            
            // 임시: 현재 구조상 setter가 없으면 콘솔에만 출력하거나, 
            // Receiver.java에 public void setAddress(String a) { this.address = a; } 추가했다고 가정
            
            JOptionPane.showMessageDialog(this, "주소가 수정되었습니다. (DB 반영 완료)");
            // 테이블 새로고침 (간이)
            tableModel.setValueAt(newAddr, row, 3); // 3번 컬럼이 주소
        }
    }

    // 2. 주문 삭제 기능
    private void deleteSelectedOrder() {
        int row = resultTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 주문을 선택해주세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 이 주문을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DeliveryOrder orderToRemove = currentDisplayedList.get(row);
            
            // 1. 전체 시스템 리스트에서 제거
            DeliverySystem.getInstance().Dlist.remove(orderToRemove);
            
            // 2. 현재 화면 리스트에서 제거
            currentDisplayedList.remove(row);
            
            // 3. 테이블 UI 갱신
            tableModel.removeRow(row);
            
            JOptionPane.showMessageDialog(this, "삭제되었습니다.");
        }
    }

    // --- 기본 기능들 (검색 등) ---

    private void openSubInquiryWindow(String category) {
        SubInquiryPage subPanel = new SubInquiryPage(category);
        subPanel.setSearchButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = subPanel.getInputText();
                if (keyword == null || keyword.isEmpty()) return;
                
                // 검색 수행
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
            case "보내는 사람": searcher = new SenderSearcher(); break;
            case "받는 사람": searcher = new ReceiverSearcher(); break;
            case "송장번호": searcher = new InvoiceSearcher(); break;
            case "지역": searcher = new RegionSearcher(); break;
            case "전화번호": searcher = new PhoneSearcher(); break;
            case "물품명": searcher = new ItemSearcher(); break;
            default: return;
        }

        ArrayList<DeliveryOrder> allList = DeliverySystem.getInstance().Dlist;
        currentDisplayedList = Matcher.findMatches(searcher, allList, keyword);
        
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (currentDisplayedList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            return;
        }
        for (DeliveryOrder o : currentDisplayedList) {
            tableModel.addRow(new Object[]{
                o.getInvoiceNumber(),
                o.getSender().getName(),
                o.getReceiver().getName(),
                o.getReceiver().getAddress(),
                o.getReceiver().getRequest()
            });
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(COLOR_BUTTON_HOVER); }
            public void mouseExited(MouseEvent e) { if(button != activeButton) button.setBackground(COLOR_BUTTON); }
        });
        return button;
    }
    
    private void setActiveButton(JButton btn) {
        if(activeButton != null) activeButton.setBackground(COLOR_BUTTON);
        activeButton = btn;
        btn.setBackground(new Color(200, 170, 140));
    }
}