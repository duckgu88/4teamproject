package guitool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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
import guitool.UITheme; // UITheme 임포트

public class ShippingPage extends JPanel {

    private MainFrame mainFrame;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private DefaultTableModel tableModel;
    private JTable table;
    private DefaultTableModel summaryTableModel;
    private JTable summaryTable;
    private JLabel mapLabel;
    private JSplitPane splitPane;

    private List<DeliveryOrder> originalList;
    private String currentActiveCard = "TABLE";

    private JButton btnShowAll;
    private JButton btnSortSender;
    private JButton btnSortReceiver;
    private JButton btnGroupByRegion;
    private JButton btnFilterRequests;
    private JButton backButton; // 뒤로가기 버튼
    private JButton btnFilterStatus; // 통합된 상태 필터 버튼
    private JButton advanceDayButton;
    private JButton btnGoToInquiry; // '주문 관리'로 가는 버튼 추가

    private int deliveryStatusFilterState = 0; // 0: 전체, 1: 배송 전, 2: 배송 중, 3: 배송 완료

    // 날짜 표시용 라벨 (배송 상태 순 버튼 자리를 대체)
    private JLabel dateLabel;

    private JButton activeButton = null;

    // --- [신규] 지역별 색상 매핑 ---
    private static final Map<String, Color> REGION_COLOR_MAP = new TreeMap<>();
    static {
        REGION_COLOR_MAP.put("경기도", new Color(253, 226, 147)); // 노란색
        REGION_COLOR_MAP.put("서울특별시", new Color(255, 158, 102)); // 주황색
        REGION_COLOR_MAP.put("강원도", new Color(248, 182, 83)); // 황토색
        REGION_COLOR_MAP.put("충청남도", new Color(133, 117, 215)); // 파란-보라색
        REGION_COLOR_MAP.put("충청북도", new Color(100, 196, 185)); // 청록색
        REGION_COLOR_MAP.put("전라북도", new Color(246, 140, 93)); // 주황색
        REGION_COLOR_MAP.put("경상북도", new Color(111, 189, 112)); // 초록색
        REGION_COLOR_MAP.put("대구광역시", new Color(118, 172, 215)); // 하늘색
        REGION_COLOR_MAP.put("전라남도", new Color(59, 168, 153)); // 진한 청록색
        REGION_COLOR_MAP.put("경상남도", new Color(170, 112, 180)); // 자주색
        REGION_COLOR_MAP.put("제주특별자치도", new Color(237, 85, 94)); // 빨간색
        REGION_COLOR_MAP.put("대구광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("광주광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("울산광역시", new Color(118, 172, 215));
        REGION_COLOR_MAP.put("부산광역시", new Color(192, 137, 201));
        REGION_COLOR_MAP.put("인천광역시", new Color(253, 226, 147));
    }

    public ShippingPage(MainFrame mainFrame) { // 생성자에서 MainFrame을 받음
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.COLOR_BACKGROUND);
        originalList = DeliverySystem.getInstance().Dlist;

        setupUI();
        addListeners();

        setActiveButton(btnShowAll);
        refreshTable(new ArrayList<>(originalList));

        cardLayout.show(cardPanel, "TABLE");
    }

    private void setupUI() {
        // ================= 상단 패널 레이아웃 =================
        JPanel buttonPanel = new JPanel(new BorderLayout(2, 0)); // BorderLayout 간격 2px
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        // --- West: 네비게이션 버튼 ---
        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); // FlowLayout 간격 2px
        navButtonsPanel.setBackground(UITheme.COLOR_BACKGROUND);
        backButton = UITheme.createStyledButton("뒤로가기");
        navButtonsPanel.add(backButton);
        
        buttonPanel.add(navButtonsPanel, BorderLayout.WEST);

        // --- Center: 기능 버튼 ---
        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 0, 2, 0)); // GridLayout 간격 2px
        actionButtonsPanel.setBackground(UITheme.COLOR_BACKGROUND);
        btnShowAll = UITheme.createStyledButton("전체 보기");
        btnSortSender = UITheme.createStyledButton("보내는 사람 순");
        btnSortReceiver = UITheme.createStyledButton("받는 사람 순");
        btnGroupByRegion = UITheme.createStyledButton("지역 별 요약");
        btnFilterRequests = UITheme.createStyledButton("요청사항 보기");
        btnFilterStatus = UITheme.createStyledButton("배송 상태: 전체"); // 통합된 상태 필터 버튼

        actionButtonsPanel.add(btnShowAll);
        actionButtonsPanel.add(btnSortSender);
        actionButtonsPanel.add(btnSortReceiver);
        actionButtonsPanel.add(btnGroupByRegion);
        actionButtonsPanel.add(btnFilterRequests);
        actionButtonsPanel.add(btnFilterStatus);

        buttonPanel.add(actionButtonsPanel, BorderLayout.CENTER);

        // --- East: 시스템 버튼 및 페이지 전환 ---
        JPanel systemButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0)); // FlowLayout 간격 2px
        systemButtonsPanel.setBackground(UITheme.COLOR_BACKGROUND);
        
        btnGoToInquiry = UITheme.createStyledButton("주문 관리");
        systemButtonsPanel.add(btnGoToInquiry);

        dateLabel = new JLabel("현재 날짜: " + DeliverySystem.getCurrentDate().toString());
        dateLabel.setForeground(UITheme.COLOR_TEXT);
        dateLabel.setFont(UITheme.FONT_BUTTON);
        systemButtonsPanel.add(dateLabel);

        advanceDayButton = UITheme.createStyledButton("날짜 갱신");
        systemButtonsPanel.add(advanceDayButton);
        
        buttonPanel.add(systemButtonsPanel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JScrollPane tableScrollPane = createTablePanel();
        cardPanel.add(tableScrollPane, "TABLE");

        splitPane = createSummarySplitPane();
        cardPanel.add(splitPane, "SUMMARY");

        add(buttonPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = { "송장번호", "보내는 사람", "연락처", "물품", "받는 사람", "주소", "요청사항", "배송 상태" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = UITheme.createStyledTable(tableModel);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        // 모델 인덱스를 뷰 인덱스로 변환
                        int modelRow = table.convertRowIndexToModel(row);
                        DeliveryOrder order = originalList.get(modelRow);
                        new WaybillDialog(mainFrame, order).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        scrollPane.setBackground(UITheme.COLOR_BACKGROUND);

        return scrollPane;
    }

    // --- [수정] 지역별 요약 SplitPane 생성 ---
    private JSplitPane createSummarySplitPane() {
        mapLabel = new JLabel();
        try {
            // 1. 이미지 경로 설정
            ImageIcon mapIcon = new ImageIcon(getClass().getResource("/image/korea.png"));

            if (mapIcon.getImage() == null) {
                throw new Exception("이미지 리소스를 찾을 수 없습니다.");
            }

            // 높이 기준으로 크기 조절
            Image image = mapIcon.getImage().getScaledInstance(-1, 500, Image.SCALE_SMOOTH);
            mapLabel.setIcon(new ImageIcon(image));

        } catch (Exception e) {
            mapLabel.setText("지도 로드 실패: /guitool/image/korea.png");
            e.printStackTrace();
        }
        mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mapLabel.setBackground(UITheme.COLOR_BACKGROUND);
        mapLabel.setOpaque(true);

        // 테이블 생성
        String[] summaryColumnNames = { "지역", "이름", "색상" };
        summaryTableModel = new DefaultTableModel(summaryColumnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Color.class;
                return super.getColumnClass(column);
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        summaryTable = new JTable(summaryTableModel);
        setupSummaryTable(summaryTable);

        TableColumn colorColumn = summaryTable.getColumnModel().getColumn(2);
        colorColumn.setCellRenderer(new ColorSquareRenderer());
        colorColumn.setMaxWidth(60);

        JScrollPane summaryTableScrollPane = new JScrollPane(summaryTable);
        summaryTableScrollPane.getViewport().setBackground(UITheme.COLOR_BACKGROUND);
        summaryTableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapLabel, summaryTableScrollPane);

        split.setDividerLocation(400);
        split.setResizeWeight(0.5);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setBackground(UITheme.COLOR_BACKGROUND);

        return split;
    }

    private void setupSummaryTable(JTable table) {
        table.setFont(UITheme.FONT_LABEL);
        table.setForeground(UITheme.COLOR_TEXT);
        table.setGridColor(UITheme.COLOR_GRID);
        table.setRowHeight(25);
        table.setSelectionBackground(UITheme.COLOR_BUTTON_HOVER);
        table.setSelectionForeground(UITheme.COLOR_TEXT);
        table.setBackground(UITheme.COLOR_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.COLOR_TABLE_HEADER);
        header.setForeground(UITheme.COLOR_TEXT);
        header.setFont(UITheme.FONT_TABLE_HEADER);
        header.setPreferredSize(new Dimension(100, 30));

        table.setDefaultRenderer(Color.class, new ColorSquareRenderer());
    }



    private void refreshTable(List<DeliveryOrder> listToShow) {
        tableModel.setRowCount(0);
        for (DeliveryOrder order : listToShow) {
            Object[] rowData = {
                    order.getInvoiceNumber(),
                    order.getSender().getName(),
                    order.getSender().getPhone(),
                    order.getSender().getItem(),
                    order.getReceiver().getName(),
                    order.getReceiver().getAddress(),
                    order.getReceiver().getRequest(),
                    order.getReceiver().getFormattedDeliveryStatus() // 배송 상태
            };
            tableModel.addRow(rowData);
        }
    }

    private void updateRegionSummaryTable() {
        summaryTableModel.setRowCount(0);

        Map<String, List<String>> regionMap = originalList.stream()
                .collect(Collectors.groupingBy(
                        order -> getRegionFromAddress(order.getReceiver().getAddress()),
                        () -> new TreeMap<>(),
                        Collectors.mapping(
                                order -> order.getReceiver().getName(),
                                Collectors.toList())));

        for (Map.Entry<String, List<String>> entry : regionMap.entrySet()) {
            String region = entry.getKey();
            List<String> names = entry.getValue();

            String uniqueNames = names.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            Color regionColor = REGION_COLOR_MAP.getOrDefault(region, Color.LIGHT_GRAY);
            summaryTableModel.addRow(new Object[] { region, uniqueNames, regionColor });
        }
    }

    private void setActiveButton(JButton clickedButton) {
        if (activeButton != null) {
            activeButton.setBackground(UITheme.COLOR_BUTTON);
        }
        clickedButton.setBackground(UITheme.COLOR_BUTTON_ACTIVE);
        this.activeButton = clickedButton;
    }

    private String getRegionFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) return "기타";
        String[] parts = address.split(" ");
        if (parts.length > 0) return parts[0];
        return "기타";
    }

    private void addListeners() {
        btnShowAll.addActionListener(e -> {
            setActiveButton(btnShowAll);
            refreshTable(new ArrayList<>(originalList));
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnSortSender.addActionListener(e -> {
            setActiveButton(btnSortSender);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            listToShow.sort(Comparator.comparing(o -> o.getSender().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnSortReceiver.addActionListener(e -> {
            setActiveButton(btnSortReceiver);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            listToShow.sort(Comparator.comparing(o -> o.getReceiver().getName()));
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnFilterRequests.addActionListener(e -> {
            setActiveButton(btnFilterRequests);
            List<DeliveryOrder> listToShow = originalList.stream()
                    .filter(order -> {
                        String request = order.getReceiver().getRequest();
                        return request != null && !request.trim().isEmpty();
                    })
                    .collect(Collectors.toList());
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });
        btnGroupByRegion.addActionListener(e -> {
            setActiveButton(btnGroupByRegion);
            updateRegionSummaryTable();
            cardLayout.show(cardPanel, "SUMMARY");
            currentActiveCard = "SUMMARY";
        });

        // '주문 관리' 페이지로 이동
        btnGoToInquiry.addActionListener(e -> {
            mainFrame.showCard("INQUIRY");
        });

        // 뒤로가기 버튼 리스너
        backButton.addActionListener(e -> {
            mainFrame.showCard("LOGIN");
        });

        // 통합된 배송 상태 필터링 리스너
        btnFilterStatus.addActionListener(e -> {
            deliveryStatusFilterState = (deliveryStatusFilterState + 1) % 4;
            setActiveButton(btnFilterStatus);
            List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
            String statusFilter = "";
            String buttonText = "배송 상태: ";

            switch (deliveryStatusFilterState) {
                case 1: // 배송 전
                    statusFilter = "배송전";
                    buttonText += statusFilter;
                    break;
                case 2: // 배송 중
                    statusFilter = "배송중";
                    buttonText += statusFilter;
                    break;
                case 3: // 배송 완료
                    statusFilter = "배송완료";
                    buttonText += statusFilter;
                    break;
                default: // 0, 전체
                    buttonText += "전체";
                    break;
            }

            btnFilterStatus.setText(buttonText);

            if (!statusFilter.isEmpty()) {
                final String finalStatusFilter = statusFilter;
                listToShow = originalList.stream()
                        .filter(order -> finalStatusFilter.equals(order.getReceiver().getStatus()))
                        .collect(Collectors.toList());
            }
            
            refreshTable(listToShow);
            cardLayout.show(cardPanel, "TABLE");
            currentActiveCard = "TABLE";
        });

        // 날짜 갱신 버튼 이벤트 리스너
        advanceDayButton.addActionListener(e -> {
            DeliverySystem.advanceDate();
            DeliverySystem.getInstance().updateDeliveryStatuses();

            if (activeButton == btnShowAll) {
                refreshTable(new ArrayList<>(originalList));
            } else if (activeButton == btnSortSender) {
                List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
                listToShow.sort(Comparator.comparing(o -> o.getSender().getName()));
                refreshTable(listToShow);
            } else if (activeButton == btnSortReceiver) {
                List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
                listToShow.sort(Comparator.comparing(o -> o.getReceiver().getName()));
                refreshTable(listToShow);
            } else if (activeButton == btnFilterRequests) {
                List<DeliveryOrder> listToShow = originalList.stream()
                        .filter(order -> {
                            String request = order.getReceiver().getRequest();
                            return request != null && !request.trim().isEmpty();
                        })
                        .collect(Collectors.toList());
                refreshTable(listToShow);
            } else if (activeButton == btnFilterStatus) {
                // 통합된 상태 필터에 대한 로직
                List<DeliveryOrder> listToShow = new ArrayList<>(originalList);
                String statusFilter = "";
                switch (deliveryStatusFilterState) {
                    case 1: statusFilter = "배송전"; break;
                    case 2: statusFilter = "배송중"; break;
                    case 3: statusFilter = "배송완료"; break;
                }
                if (!statusFilter.isEmpty()) {
                    final String finalStatusFilter = statusFilter;
                    listToShow = originalList.stream()
                            .filter(order -> finalStatusFilter.equals(order.getReceiver().getStatus()))
                            .collect(Collectors.toList());
                }
                refreshTable(listToShow);
            } else { // Default to show all if no active filter/sort (btnGroupByRegion 포함)
                refreshTable(new ArrayList<>(originalList));
            }

            // 지역 요약 테이블 갱신
            updateRegionSummaryTable();
            // 상단 날짜 라벨도 갱신
            dateLabel.setText("현재 날짜: " + DeliverySystem.getCurrentDate().toString());

            JOptionPane.showMessageDialog(this,
                    "현재 날짜: " + DeliverySystem.getCurrentDate().toString() +
                    "\n배송 상태가 업데이트되었습니다.");
        });
    }

    private int getDeliveryStatusOrder(String status) {
        return switch (status) {
            case "배송중" -> 1;
            case "배송전" -> 2;
            case "배송완료" -> 3;
            default -> 99; // 기타 알 수 없는 상태
        };
    }

    class ColorSquareRenderer extends JPanel implements TableCellRenderer {
        public ColorSquareRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Color) setBackground((Color) value);
            else setBackground(Color.WHITE);

            if (isSelected) setBorder(BorderFactory.createLineBorder(table.getSelectionBackground().darker()));
            else setBorder(null);
            return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
}

