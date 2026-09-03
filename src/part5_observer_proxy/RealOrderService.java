package part5_observer_proxy;

import part1_singleton.Order;
import part1_singleton.QuickBiteSystem;
import part2_abstract_factory.RegionFactory;
import part4_bridge.NotificationChannel;

public class RealOrderService implements OrderService {
    @Override
    public void placeOrder(Order order, RegionFactory regionFactory, NotificationChannel channel) {
        QuickBiteSystem.getInstance().processOrder(order, regionFactory, channel);
    }
}
