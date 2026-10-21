package part9_memento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Memento class for the Memento Pattern.
 * Immutable snapshot of cart items, coupon, subtotal, and notes.
 */
public class OrderMemento {

    private final String snapshotId;
    private final List<String> cartItems;
    private final double subtotal;
    private final String appliedCoupon;
    private final String instructions;
    private final Date savedAt;

    public OrderMemento(String snapshotId, List<String> cartItems, double subtotal, String appliedCoupon, String instructions) {
        this.snapshotId = snapshotId;
        this.cartItems = new ArrayList<>(cartItems); // defensive copy
        this.subtotal = subtotal;
        this.appliedCoupon = appliedCoupon;
        this.instructions = instructions;
        this.savedAt = new Date();
    }

    public String getSnapshotId() { return snapshotId; }
    public List<String> getCartItems() { return new ArrayList<>(cartItems); }
    public double getSubtotal() { return subtotal; }
    public String getAppliedCoupon() { return appliedCoupon; }
    public String getInstructions() { return instructions; }
    public Date getSavedAt() { return savedAt; }

    @Override
    public String toString() {
        return "OrderMemento #" + snapshotId + " [Items=" + cartItems + ", Subtotal=" + subtotal + ", Coupon=" + appliedCoupon + "]";
    }
}
