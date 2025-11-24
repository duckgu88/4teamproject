package search;

import main.DeliveryOrder;

public class PhoneSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if (order.getSender().getPhone().equals(keyword) || order.getReceiver().getPhone().equals(keyword)) {
            return true;
        }
        return false;
    }
}
