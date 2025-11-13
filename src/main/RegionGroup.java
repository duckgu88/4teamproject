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
}
