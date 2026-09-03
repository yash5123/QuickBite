package part5_observer_proxy;

import part1_singleton.Order;
import part2_abstract_factory.RegionFactory;
import part4_bridge.NotificationChannel;

// F_4
public interface OrderService {
    void placeOrder(Order order, RegionFactory regionFactory, NotificationChannel channel);
}
