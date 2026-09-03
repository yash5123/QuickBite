package part5_observer_proxy;

import part1_singleton.Order;

public class CustomerAppNotifier implements DeliveryObserver {
    @Override
    public void update(Order order, DeliveryStatus status) {
        System.out.println("[CUSTOMER APP] " + order.getCustomerName() + "'s order is now: " + status);
    }
}
