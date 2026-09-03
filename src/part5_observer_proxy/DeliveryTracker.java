package part5_observer_proxy;

import part1_singleton.Order;
import java.util.ArrayList;
import java.util.List;

// F_3
public class DeliveryTracker {

    private final Order order;
    private final List<DeliveryObserver> observers = new ArrayList<>();
    private DeliveryStatus status = DeliveryStatus.PLACED;

    public DeliveryTracker(Order order) {
        this.order = order;
    }

    public void addObserver(DeliveryObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DeliveryObserver observer) {
        observers.remove(observer);
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
        notifyObservers();
    }

    private void notifyObservers() {
        for (DeliveryObserver observer : observers) {
            observer.update(order, status);
        }
    }
}
