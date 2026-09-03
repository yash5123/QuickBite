package part5_observer_proxy;

import part1_singleton.Order;
import part2_abstract_factory.RegionFactory;
import part4_bridge.NotificationChannel;

// F_5
public class OrderServiceProxy implements OrderService {

    private RealOrderService realOrderService;
    private final String username;
    private final String password;

    private static final String VALID_USERNAME = "customer1";
    private static final String VALID_PASSWORD = "pass123";

    public OrderServiceProxy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private boolean authenticate() {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }

    @Override
    public void placeOrder(Order order, RegionFactory regionFactory, NotificationChannel channel) {
        System.out.println("\nAuthenticating user: " + username + " ...");
        if (authenticate()) {
            System.out.println("Login successful. Access granted.");
            if (realOrderService == null) {
                realOrderService = new RealOrderService();
            }
            realOrderService.placeOrder(order, regionFactory, channel);
        } else {
            System.out.println("Login failed. Invalid credentials. Access denied.");
            System.out.println("Order for " + order.getCustomerName() + " was NOT placed.");
        }
    }
}
