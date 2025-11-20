package main;

import java.util.ArrayList;

public class RegionGroup {
    String region;
    String driverName;
    ArrayList<DeliveryOrder> orders = new ArrayList<>();

    public RegionGroup(String region) {
        this.region = region;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRegion() {
        return region;
    }

    public void addOrder(DeliveryOrder order) {
        orders.add(order);
    }

    public void print() {
        System.out.println("[" + region + "] 기사님: " + driverName);
        for (DeliveryOrder d : orders)
            d.print();
        System.out.println();
    }
    
    public String getInfoString() {
        // (기존 코드와 동일 - JTextArea 버전에서 사용됨)
        StringBuilder sb = new StringBuilder();
        String driver = (driverName != null) ? driverName : "배정 안됨";
        sb.append("========== [ " + region + " ] ==========\n");
        sb.append("담당 기사: " + driver + "\n\n");
        if (orders.isEmpty()) {
            sb.append("  (이 지역에 배정된 주문이 없습니다.)\n");
        } else {
            for (DeliveryOrder d : orders) {
                sb.append(d.getInfoString()); 
                sb.append("----------------------------\n");
            }
        }
        sb.append("===============================\n\n");
        return sb.toString();
    }
    
    // [getter 추가] 테이블 표시에 필요
    public String getDriverName() {
        return (driverName != null) ? driverName : "배정 안됨";
    }

    // [getter 추가] 테이블 표시에 필요
    public ArrayList<DeliveryOrder> getOrders() {
        return orders;
    }
}