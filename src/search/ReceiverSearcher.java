package search;

import main.DeliveryOrder;

public class ReceiverSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if order.receiver.name.equals(keyword):
            return true;
        return false;
    }
}
