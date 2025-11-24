package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class RegionManager {
    public ArrayList<RegionGroup> rList = new ArrayList<>();

    public void groupRegion(ArrayList<DeliveryOrder> Dlist) {
        for (DeliveryOrder d : Dlist) {
            // [수정] Getter 사용 및 오타 수정 (d.receiver.getAdderss() -> d.getReceiver().getAddress())
            String[] Address = d.getReceiver().getAddress().split(" ");
            String region = Address[0];

            RegionGroup group = findRegionGroup(region);
            if (group == null) {
                group = new RegionGroup(region);
                rList.add(group);
            }
            group.addOrder(d);
        }
    }

    public RegionGroup findRegionGroup(String region) {
        for (RegionGroup g : rList)
            if (g.getRegion().equals(region))
                return g;
        return null;
    }

    public void assignDrivers(String filename) {
        try (Scanner scan = new Scanner(new File(filename))) {
            while (scan.hasNext()) {
                String region = scan.next();
                String driver = scan.next();

                RegionGroup group = findRegionGroup(region);
                if (group != null)
                    group.setDriverName(driver);
            }
        } catch (Exception e) {
            System.out.println("drivers.txt 읽기 실패");
        }
    }

    public void printRegion() {
        for (RegionGroup g : rList)
            g.print();
    }

    public String getAllRegionsInfoString() {
        if (rList == null || rList.isEmpty()) {
            return "표시할 배송 지역 정보가 없습니다.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- 전체 지역 배송 현황 ---\n\n");
        
        for (RegionGroup g : rList) {
            sb.append(g.getInfoString());
        }
        return sb.toString();
    }
}