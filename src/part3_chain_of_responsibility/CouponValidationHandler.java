package part3_chain_of_responsibility;

import part1_singleton.Order;

// G_2
public class CouponValidationHandler extends OrderHandler {
    @Override
    public boolean handle(Order order) {
        if (order.getCouponCode() != null && order.getCouponCode().equalsIgnoreCase("INVALID")) {
            System.out.println("Coupon check FAILED for " + order.getCustomerName() + ". Order rejected.");
            return false;
        }
        System.out.println("Coupon check passed for " + order.getCustomerName() + ".");
        return next == null || next.handle(order);
    }
}
