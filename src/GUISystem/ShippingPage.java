package GUISystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap; 
import java.util.stream.Collectors;

import main.DeliveryOrder;
import main.DeliverySystem;

public class ShippingPage extends JFrame {

    // --- [디자인] 사용할 색상 정의 ---
    private static final Color COLOR_BACKGROUND = new Color(239, 222, 207);
    private static final Color COLOR_BUTTON = new Color(225, 205, 188);
    private static final Color COLOR_BUTTON_HOVER = new Color(218, 184, 153);
    private static final Color COLOR_BUTTON_ACTIVE = new Color(200, 170, 140);
    private static final Color COLOR_TABLE_HEADER = new Color(218, 184, 153);
    private static final Color COLOR_TEXT = new Color(77, 77, 77);
    private static final Color COLOR_GRID = new Color(220, 200, 185);
    private static final Color COLOR_ROW_ALT = new Color(247, 241, 235);

    // --- [신규] 뷰 전환을 위한 컴포넌트 ---
    private CardLayout cardLayout;
    private JPanel cardPanel; 
    private JTextArea summaryArea; 

    // --- 기존 멤버 변수 ---
    private DefaultTableModel tableModel;
    private JTable table;
    private List<DeliveryOrder> originalList;

    private JButton btnShowAll;
    private JButton btnSortSender;
    private JButton btnSortReceiver;
    private JButton btnGroupByRegion;
    private JButton btnFilterRequests;
    
    private JButton activeButton = null;

    public ShippingPage() {
        setTitle("배송 관리"); 
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(COLOR_BACKGROUND);
        originalList = DeliverySystem.getInstance().Dlist;

        setupUI();
        addListeners();

        setActiveButton(btnShowAll); 
        refreshTable(new ArrayList<>(originalList));
        
        cardLayout.show(cardPanel, "TABLE");
        
        setVisible(true);
    }

    private void setupUI() {
        // 1. 상단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        btnShowAll = createStyledButton("전체 보기");
        btnSortSender = createStyledButton("보내는 사람 순");
        btnSortReceiver = createStyledButton("받는 사람 이름 순");
        btnGroupByRegion = createStyledButton("지역 별 요약"); 
        btnFilterRequests = createStyledButton("요청사항 보기");

        buttonPanel.add(btnShowAll);
        buttonPanel.add(btnSortSender);
        buttonPanel.add(btnSortReceiver);
        buttonPanel.add(btnGroupByRegion);
        buttonPanel.add(btnFilterRequests);

        // --- 2. [신규] CardLayout 패널 설정 ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BACKGROUND); 

        // --- 3. [기존] "TABLE" 카드 (JTable) ---
        JScrollPane tableScrollPane = createTablePanel(); 
        cardPanel.add(tableScrollPane, "TABLE"); 

        // --- 4. [신규] "SUMMARY" 카드 (JTextArea) ---
        JScrollPane summaryScrollPane = createSummaryPanel(); 
        cardPanel.add(summaryScrollPane, "SUMMARY"); 

        // --- 5. 프레임에 패널 추가 ---
        add(buttonPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER); 
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"송장번호", "보내는 사람", "연락처", "물품", "받는 사람", "주소", "요청사항"};
        tableModel = new DefaultTableModel(columnNames, 0);
        
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? COLOR_BACKGROUND : COLOR_ROW_ALT);
                    c.setForeground(COLOR_TEXT);
                } else {
                    c.setBackground(COLOR_BUTTON_HOVER);
                    c.setForeground(COLOR_TEXT);
                }
                return c;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_GRID);
        table.setRowHeight(24);
        table.setSelectionBackground(COLOR_BUTTON_HOVER);
        table.setSelectionForeground(COLOR_TEXT);
        table.setBackground(COLOR_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 30));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
        scrollPane.setBackground(COLOR_BACKGROUND);
        
        return scrollPane;
    }

    private JScrollPane createSummaryPanel() {
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setBackground(COLOR_BACKGROUND); 
        summaryArea.setForeground(COLOR_TEXT);       
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); 
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
        
        return scrollPane;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(COLOR_BUTTON);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button == activeButton) {
                    button.setBackground(COLOR_BUTTON_ACTIVE);
                } else {
                    button.setBackground(COLOR_BUTTON);
                }
            }
        });
        return button;
    }

    /**
     * [수정] JTable을 새로고침 (Getter 사용 및 오타 수정)
     */
    private void refreshTable(List<DeliveryOrder> listToShow) {
        tableModel.setRowCount(0); 

        for (DeliveryOrder order : listToShow) {
            Object[] rowData = {
                // [수정] Getter를 사용하도록 변경
                order.getInvoiceNumber(),
                order.getSender().getName(),
                order.getSender().getPhone(),
                order.getSender().getItem(),
                order.getReceiver().getName(),
                order.getReceiver().getAddress(), // [수정] 오타 수정 (getAdderss -> getAddress)
                order.getReceiver().getRequest()
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * [수정] 지역별 요약 텍스트 생성 (Getter 사용 및 오타 수정)
     */
    private void updateRegionSummary() {
        Map<String, List<String>> regionMap = originalList.stream()
            .collect(Collectors.groupingBy(
                // [수정] Getter 사용 및 오타 수정
                order -> getRegionFromAddress(order.getReceiver().getAddress()), 
                () -> new TreeMap<>(), 
                Collectors.mapping(
                    order -> order.getReceiver().getName(), 
                    Collectors.toList() 
                )
            ));

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : regionMap.entrySet()) {
            String region = entry.getKey();
            List<String> names = entry.getValue();
            
            String uniqueNames = names.stream()
                                      .distinct()
                                      .collect(Collectors.joining(", ")); 
            
            sb.append(region).append(" 지역 : ");
            sb.append(uniqueNames);
            sb.append("\n\n"); 
        }

        summaryArea.setText(sb.toString());
        summaryArea.setCaretPosition(0); 
    }

    private void setActiveButton(JButton clickedButton) {
        if (activeButton != null) {
            activeButton.setBackground(COLOR_BUTTON);
        }
        clickedButton.setBackground(COLOR_BUTTON_ACTIVE);
        this.activeButton = clickedButton;
    }

    private String getRegionFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "기타";
        }
        return address.split(" ")[0];
    }

    private void addListeners() {
        
        btnShowAll.addActionListener(e -> {
            setActiveButton(btnShowAll);
            refreshTable(new ArrayList<>(originalList));
            cardLayout.show(cardPanel, "TABLE"); 
        });

        btnSortSender.addActionListener(e -> {
            setActiveButton(btnSortSender);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            // [수정] Getter 사용
            listToShow.sort(Comparator.comparing(o -> o.getSender().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE"); 
        });

        btnSortReceiver.addActionListener(e -> {
            setActiveButton(btnSortReceiver);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            // [수정] Getter 사용
            listToShow.sort(Comparator.comparing(o -> o.getReceiver().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE"); 
        });

        btnFilterRequests.addActionListener(e -> {
            setActiveButton(btnFilterRequests);
            List<DeliveryOrder> listToShow = originalList.stream()
                .filter(order -> {
                    // [수정] Getter 사용
                    String request = order.getReceiver().getRequest();
                    return request != null && !request.trim().isEmpty();
                })
                .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE"); 
        });

        btnGroupByRegion.addActionListener(e -> {
            setActiveButton(btnGroupByRegion);
            updateRegionSummary(); 
            cardLayout.show(cardPanel, "SUMMARY"); 
        });
    }
}