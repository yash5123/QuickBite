package part1_singleton;

import part2_abstract_factory.*;
import part3_chain_of_responsibility.*;
import part4_bridge.*;
import part5_observer_proxy.*;

// P_2
public class QuickBiteSystem {

    private static QuickBiteSystem instance;

    private QuickBiteSystem() {
        System.out.println("QuickBiteSystem instance created.");
    }

    public static QuickBiteSystem getInstance() {
        if (instance == null) {
            synchronized (QuickBiteSystem.class) {
                if (instance == null) {
                    instance = new QuickBiteSystem();
                }
            }
        }
        return instance;
    }

    public void processOrder(Order order, RegionFactory regionFactory, NotificationChannel channel) {
        System.out.println("\n----- QuickBiteSystem: Processing order for " + order.getCustomerName() + " -----");

        TaxCalculator taxCalculator = regionFactory.createTaxCalculator();
        PaymentProcessor paymentProcessor = regionFactory.createPaymentProcessor();

        double tax = taxCalculator.calculateTax(order.getAmount());
        order.setTax(tax);
        order.setPaymentProcessor(paymentProcessor);

        OrderHandler couponHandler = new CouponValidationHandler();
        OrderHandler fraudHandler = new FraudCheckHandler();
        OrderHandler paymentHandler = new PaymentGatewayHandler();

        couponHandler.setNext(fraudHandler);
        fraudHandler.setNext(paymentHandler);

        boolean success = couponHandler.handle(order);

        if (success) {
            Notification confirmation = new OrderConfirmedNotification(channel);
            confirmation.send(order.getCustomerName());

            DeliveryTracker tracker = new DeliveryTracker(order);
            tracker.addObserver(new CustomerAppNotifier());
            tracker.addObserver(new RestaurantDashboard());
            tracker.addObserver(new PartnerAppNotifier());

            tracker.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
            Notification outForDelivery = new OutForDeliveryNotification(channel);
            outForDelivery.send(order.getCustomerName());

            tracker.setStatus(DeliveryStatus.DELIVERED);
        } else {
            System.out.println("Order for " + order.getCustomerName() + " could not be processed.");
        }
    }
}
