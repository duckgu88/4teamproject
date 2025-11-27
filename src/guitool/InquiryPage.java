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
import guitool.UITheme; // UITheme 임포트

public class InquiryPage extends JPanel {

    private MainFrame mainFrame; // MainFrame 참조

    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton activeButton = null;
    private JButton advanceDayButton;
    private JLabel dateLabel; // 현재 날짜 표시용 라벨
    private InquiryPresenter presenter; // 프레젠터 필드 추가

    // 현재 조회된 리스트를 저장 (수정/삭제 시 인덱스 매핑용)
    private ArrayList<DeliveryOrder> currentDisplayedList = new ArrayList<>();

    public InquiryPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_BACKGROUND);

        this.presenter = new InquiryPresenter(this); // 프레젠터 초기화

        setupUI();
    }

    private void setupUI() {
        // ================= 상단 패널 레이아웃 변경 (한 줄, 간격/크기 조절) =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.COLOR_BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        // --- 왼쪽: 뒤로가기 + 카테고리 버튼 ---
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 간격 10->5
        topLeftPanel.setBackground(UITheme.COLOR_BACKGROUND);

        Dimension buttonSize = UITheme.BUTTON_DIMENSION_SMALL; // 너비 110->100

        JButton backButton = UITheme.createStyledButton("뒤로가기");
        backButton.setPreferredSize(buttonSize);
        backButton.addActionListener(e -> {
            mainFrame.showCard("SHIPPING"); // 배송 관리 페이지로 돌아감
        });
        topLeftPanel.add(backButton);

        String[] categories = {"보내는 사람", "받는 사람", "송장번호", "지역", "전화번호", "물품명"};
        for (String cat : categories) {
            JButton btn = UITheme.createStyledButton(cat);
            btn.setPreferredSize(buttonSize);
            btn.addActionListener(e -> {
                setActiveButton(btn);
                openSubInquiryWindow(cat);
            });
            topLeftPanel.add(btn);
        }
        
        topPanel.add(topLeftPanel, BorderLayout.WEST);

        // --- 오른쪽: '배송 관리' 버튼 추가 ---
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRightPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton btnGoToShipping = UITheme.createStyledButton("배송 관리");
        btnGoToShipping.setPreferredSize(buttonSize);
        btnGoToShipping.addActionListener(e -> {
            mainFrame.showCard("SHIPPING");
        });
        topRightPanel.add(btnGoToShipping);
        
        topPanel.add(topRightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= 중앙 : 결과 테이블 =================
        JScrollPane scrollPane = createTablePanel();
        add(scrollPane, BorderLayout.CENTER);

        // ================= 하단 : 왼쪽(날짜) + 오른쪽(수정/삭제) =================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- 왼쪽 : 현재 날짜 + 날짜 갱신 버튼 ---
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomLeftPanel.setBackground(UITheme.COLOR_BACKGROUND);

        dateLabel = new JLabel();
        dateLabel.setForeground(UITheme.COLOR_TEXT);
        updateDateLabel(); // 처음 화면 띄울 때 날짜 설정
        bottomLeftPanel.add(dateLabel);

        advanceDayButton = UITheme.createStyledButton("날짜 갱신");
        advanceDayButton.setPreferredSize(buttonSize);
        bottomLeftPanel.add(advanceDayButton);

        bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);

        // --- 오른쪽 : 주소 수정 / 주문 삭제 버튼 ---
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomRightPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JButton btnEditAddress = UITheme.createStyledButton("✏️ 주소 수정");
        btnEditAddress.setBackground(UITheme.COLOR_BUTTON_SPECIAL_YELLOW); // 연한 노랑
        btnEditAddress.addActionListener(e -> editSelectedOrderAddress());

        JButton btnDelete = UITheme.createStyledButton("🗑️ 주문 삭제");
        btnDelete.setBackground(UITheme.COLOR_BUTTON_SPECIAL_RED); // 연한 빨강
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
            updateTable(currentDisplayedList, false);   // 메시지 없이 테이블만 갱신

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
        String[] columnNames = {"송장번호", "보내는 사람", "연락처", "물품명", "받는 사람", "주소", "요청사항", "배송 상태"};
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
                        ? (row % 2 == 0 ? UITheme.COLOR_BACKGROUND : UITheme.COLOR_ROW_ALT)
                        : UITheme.COLOR_BUTTON_HOVER);
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
                        new WaybillDialog(mainFrame, order).setVisible(true);
                    }
                }
            }
        });

        resultTable.setRowHeight(26);
        resultTable.getTableHeader().setBackground(UITheme.COLOR_TABLE_HEADER);
        resultTable.setBackground(UITheme.COLOR_BACKGROUND);

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.getViewport().setBackground(UITheme.COLOR_BACKGROUND);
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

        presenter.editOrderAddress(order, newAddr);
    }

    /**
     * Presenter로부터 주소 수정 성공 콜백을 받습니다.
     * @param editedOrder 수정된 주문
     * @param newAddress 새로운 주소
     */
    public void onEditAddressSuccess(DeliveryOrder editedOrder, String newAddress) {
        int rowIndex = currentDisplayedList.indexOf(editedOrder);
        if (rowIndex != -1) {
            tableModel.setValueAt(newAddress, rowIndex, 5); // 5번 컬럼이 주소
        }
        JOptionPane.showMessageDialog(this, "주소가 수정되었습니다.");
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
            presenter.deleteOrder(orderToRemove);
        }
    }

    /**
     * Presenter로부터 주문 삭제 성공 콜백을 받습니다.
     * @param deletedOrder 삭제된 주문
     */
    public void onOrderDeletionSuccess(DeliveryOrder deletedOrder) {
        int indexToRemove = currentDisplayedList.indexOf(deletedOrder);
        if (indexToRemove != -1) {
            currentDisplayedList.remove(indexToRemove);
            tableModel.removeRow(indexToRemove);
        }
        JOptionPane.showMessageDialog(this, "삭제되었습니다.");
    }

    /**
     * Presenter로부터 주문 삭제 실패 콜백을 받습니다.
     */
    public void onOrderDeletionFailure() {
        JOptionPane.showMessageDialog(this, "주문 삭제에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
    }

    private void openSubInquiryWindow(String category) {
        SubInquiryPage subPanel = new SubInquiryPage(category);
        JDialog subDialog = new JDialog(mainFrame, "조회 - " + category, true);

        subPanel.setSearchButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = subPanel.getInputText();
                presenter.performSearch(category, keyword); // Call presenter
                subDialog.dispose();
            }
        });

        subDialog.setSize(400, 150);
        subDialog.setLocationRelativeTo(mainFrame);
        subDialog.setContentPane(subPanel);
        subDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        subDialog.setVisible(true);
    }

    /**
     * Presenter로부터 받은 검색 결과로 테이블을 업데이트합니다.
     * @param orders 표시할 주문 목록
     */
    public void updateTable(ArrayList<DeliveryOrder> orders, boolean showMessage) {
        currentDisplayedList = orders;
        tableModel.setRowCount(0);

        if (orders.isEmpty()) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            }
            return;
        }

        for (DeliveryOrder o : orders) {
            tableModel.addRow(new Object[]{
                    o.getInvoiceNumber(),
                    o.getSender().getName(),
                    o.getSender().getPhone(),
                    o.getSender().getItem(),
                    o.getReceiver().getName(),
                    o.getReceiver().getAddress(),
                    o.getReceiver().getRequest(),
                    o.getReceiver().getFormattedDeliveryStatus()
            });
        }
    }

    private void setActiveButton(JButton btn) {
        if (activeButton != null) activeButton.setBackground(UITheme.COLOR_BUTTON);
        activeButton = btn;
        btn.setBackground(UITheme.COLOR_BUTTON_ACTIVE);
    }
}
