package part4_bridge;

public class OutForDeliveryNotification extends Notification {

    public OutForDeliveryNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String customerName) {
        channel.send("Hi " + customerName + ", your order is out for delivery!");
    }
}
