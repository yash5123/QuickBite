package part3_chain_of_responsibility;

import part1_singleton.Order;

// G_3
public class FraudCheckHandler extends OrderHandler {
    @Override
    public boolean handle(Order order) {
        if (order.getAmount() > 50000) {
            System.out.println("Fraud check FAILED for " + order.getCustomerName() + ". Amount too high.");
            return false;
        }
        System.out.println("Fraud check passed for " + order.getCustomerName() + ".");
        return next == null || next.handle(order);
    }
}
