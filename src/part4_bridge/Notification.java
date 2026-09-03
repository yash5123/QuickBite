package part4_bridge;

// Y_2
public abstract class Notification {

    protected NotificationChannel channel;

    public Notification(NotificationChannel channel) {
        this.channel = channel;
    }

    public abstract void send(String customerName);
}
