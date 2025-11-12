package search;

import main.DeliveryOrder;

public class SenderSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if order.sender.name.equals(keyword):
            return true;
        return false;
    }
}
