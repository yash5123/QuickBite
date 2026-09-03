package part4_bridge;

public class PushChannel implements NotificationChannel {
    @Override
    public void send(String message) {
        System.out.println("[PUSH] " + message);
    }
}
