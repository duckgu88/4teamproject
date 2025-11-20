package search;

import main.DeliveryOrder;

public class ReceiverSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        return order.getReceiver().getName().equals(keyword);
    }
}
