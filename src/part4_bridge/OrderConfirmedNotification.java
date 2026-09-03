package part4_bridge;

// Y_3
public class OrderConfirmedNotification extends Notification {

    public OrderConfirmedNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String customerName) {
        channel.send("Hi " + customerName + ", your QuickBite order has been confirmed!");
    }
}
