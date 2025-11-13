package search;

import main.DeliveryOrder;

public class InvoiceSearcher implements Searchable {
    @Override
    public boolean match(DeliveryOrder order, String keyword) {
        if String.valueOf(order.invoiceNumber).equals(keyword):
            return true;
        return false;
    }
}
