package search;

import main.DeliveryOrder;

public interface Searchable {
    boolean match(DeliveryOrder order, String keyword);
}
