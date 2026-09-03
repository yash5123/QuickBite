package part5_observer_proxy;

import part1_singleton.Order;

public class RestaurantDashboard implements DeliveryObserver {
    @Override
    public void update(Order order, DeliveryStatus status) {
        System.out.println("[RESTAURANT DASHBOARD] Order for " + order.getCustomerName() + " updated to: " + status);
    }
}
