package search;

import main.DeliveryOrder;

public class SenderSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        return order.getSender().getName().equals(keyword);
    }
}
