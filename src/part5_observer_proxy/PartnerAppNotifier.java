package part5_observer_proxy;

import part1_singleton.Order;

public class PartnerAppNotifier implements DeliveryObserver {
    @Override
    public void update(Order order, DeliveryStatus status) {
        System.out.println("[PARTNER APP] Delivery task for " + order.getCustomerName() + " is now: " + status);
    }
}
