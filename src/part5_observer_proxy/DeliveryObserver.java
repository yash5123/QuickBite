package part5_observer_proxy;

import part1_singleton.Order;

// F_2
public interface DeliveryObserver {
    void update(Order order, DeliveryStatus status);
}
