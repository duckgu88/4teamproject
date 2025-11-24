package search;

import main.DeliveryOrder;

public class ItemSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if (order.getSender().getItem().equals(keyword)) {
            return true;
        }
        return false;
    }
}
