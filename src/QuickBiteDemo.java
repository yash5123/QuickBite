// P_3
import part1_singleton.*;
import part2_abstract_factory.*;
import part4_bridge.*;
import part5_observer_proxy.*;

public class QuickBiteDemo {

    public static void main(String[] args) {

        System.out.println("===== Verifying Singleton: QuickBiteSystem =====");
        QuickBiteSystem sys1 = QuickBiteSystem.getInstance();
        QuickBiteSystem sys2 = QuickBiteSystem.getInstance();
        System.out.println("sys1 == sys2 : " + (sys1 == sys2));

        System.out.println("\n===== Order 1: Valid login, India region, Email =====");
        OrderService validAccess = new OrderServiceProxy("customer1", "pass123");
        Order order1 = new Order("Rahul Verma", 500.0, "SAVE10");
        validAccess.placeOrder(order1, new IndiaRegionFactory(), new EmailChannel());

        System.out.println("\n===== Order 2: Valid login, US region, SMS =====");
        Order order2 = new Order("Emily Davis", 40.0, "SAVE10");
        validAccess.placeOrder(order2, new USRegionFactory(), new SMSChannel());

        System.out.println("\n===== Order 3: Invalid coupon (rejected by Chain of Responsibility) =====");
        Order order3 = new Order("Aman Gupta", 300.0, "INVALID");
        validAccess.placeOrder(order3, new IndiaRegionFactory(), new PushChannel());

        System.out.println("\n===== Order 4: Invalid login (rejected by Proxy) =====");
        OrderService invalidAccess = new OrderServiceProxy("customer1", "wrongpass");
        Order order4 = new Order("Sara Khan", 200.0, "SAVE10");
        invalidAccess.placeOrder(order4, new IndiaRegionFactory(), new EmailChannel());
    }
}
