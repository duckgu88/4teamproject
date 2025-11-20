package search;

import main.DeliveryOrder;

public class CompanySearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if (order.getSender().getCompany().equals(keyword)) {
            return true;
        }
        return false;
    }
}
