package part3_chain_of_responsibility;

import part1_singleton.Order;

// G_4
public class PaymentGatewayHandler extends OrderHandler {
    @Override
    public boolean handle(Order order) {
        System.out.println("Payment gateway processing order for " + order.getCustomerName()
                + " | Amount: " + order.getAmount() + " + Tax: " + order.getTax()
                + " = " + order.getTotalPayable());
        order.getPaymentProcessor().charge(order.getTotalPayable());
        return true;
    }
}
