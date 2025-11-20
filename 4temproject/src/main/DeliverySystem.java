package main;

import java.io.File;
import java.util.*;
import mgr.*;

public class DeliverySystem {
    // 1. 유일한 인스턴스를 저장할 static 변수
    private static DeliverySystem instance;

    Scanner scan = new Scanner(System.in);
    Manager<Sender> senderMgr = new Manager<>();
    static Manager<Receiver> receiverMgr = new Manager<>();
    ArrayList<RegionGroup> regionGroups = new ArrayList<>();
    
    // 2. Dlist를 외부에서 접근할 수 있도록 public으로 변경
    public ArrayList<DeliveryOrder> Dlist = new ArrayList<>(); 
    
    // 3. RegionManager도 외부에서 접근할 수 있도록 멤버 변수로 이동
    public RegionManager rm = new RegionManager(); 

    // 4. private 생성자로 변경 (외부에서 new DeliverySystem() 방지)
    private DeliverySystem() {}

    // 5. 유일한 인스턴스를 가져오는 static 메소드
    public static DeliverySystem getInstance() {
        if (instance == null) {
            instance = new DeliverySystem();
        }
        return instance;
    }

    // 6. mymain() -> loadAllData()로 이름 변경 (데이터 로딩 역할)
    public void loadAllData() { 
        senderMgr.readAll("senders.txt", new SenderFactory());
        receiverMgr.readAll("receivers.txt", new ReceiverFactory());

        createDeliveryOrder(); // Dlist 생성

        // RegionManager 로직 수행
        rm.groupRegion(Dlist);
        rm.assignDrivers("drivers.txt");

        ArrayList<String> requests = readRequests();
        addRequests(requests, rm);

        System.out.println("데이터 로딩 완료."); // 로딩 확인용
    }

    /**
     * 7. [수정된 메소드] 송장 번호로 주문을 찾는 기능
     */
    public DeliveryOrder findOrder(String invoiceStr) {
        try {
            int invoiceNumber = Integer.parseInt(invoiceStr.trim());
            for (DeliveryOrder order : Dlist) {
                // [수정] 'order.invoiceNumber' 대신 'order.getInvoiceNumber()' Getter 사용
                if (order.getInvoiceNumber() == invoiceNumber) {
                    return order; // 찾았다!
                }
            }
        } catch (NumberFormatException e) {
            return null; // 숫자가 아닌 문자가 입력됨
        }
        return null; // 못 찾음
    }

    // --- (이하는 기존 코드) ---
    
    ArrayList<String> readRequests() {
        ArrayList<String> requests = new ArrayList<>();
        try (Scanner scan = new Scanner(new File("requests.txt"))) {
            while(scan.hasNextLine()) {
                requests.add(scan.nextLine());
            }
        } catch (Exception e) {
            System.out.println("requests.txt 읽기 실패");
        }
        return requests;
    }
    
    /**
     * [수정된 메소드] 요청사항을 추가하는 기능
     */
    private void addRequests(ArrayList<String> requests, RegionManager rm) {
        Random random = new Random();
        for(RegionGroup group : rm.rList) {
            // [권장] 'group.orders' 대신 'group.getOrders()' Getter 사용
            for (DeliveryOrder order : group.getOrders()) { 
                if(!requests.isEmpty()) {
                    // [수정] 'order.receiver.request = ...' 대신 Setter 사용
                    order.getReceiver().setRequest(requests.get(random.nextInt(requests.size())));
                }
            }
        }
    }

    void createDeliveryOrder() {
        int size = senderMgr.getList().size();
        for(int i = 0; i < size; i++) {
            Sender s = senderMgr.getList().get(i);
            Receiver r = receiverMgr.getList().get(i);
            Dlist.add(new DeliveryOrder(s,r));
        }
    }
    
    public class SenderFactory implements Factory {
        public Manageable create() {
            return new Sender();
        }
    }

    public class ReceiverFactory implements Factory {
        public Manageable create() {
            return new Receiver();
        }
    }
}