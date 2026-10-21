package part9_memento;

import java.util.ArrayList;
import java.util.List;

/**
 * Originator class for the Memento Pattern.
 * The active shopping cart that creates snapshots of itself and restores from them.
 */
public class CartOriginator {

    private List<String> items = new ArrayList<>();
    private double subtotal = 0.0;
    private String couponCode = "NONE";
    private String instructions = "";

    public void addItem(String item, double price) {
        items.add(item);
        subtotal += price;
        System.out.println("[ORIGINATOR] Added '" + item + "' (Rs. " + price + ") -> Subtotal: Rs. " + subtotal);
    }

    public void applyCoupon(String couponCode, double discountAmount) {
        this.couponCode = couponCode;
        this.subtotal = Math.max(0, this.subtotal - discountAmount);
        System.out.println("[ORIGINATOR] Applied coupon '" + couponCode + "' (Discount Rs. " + discountAmount + ") -> Subtotal: Rs. " + subtotal);
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getItems() { return new ArrayList<>(items); }
    public double getSubtotal() { return subtotal; }
    public String getCouponCode() { return couponCode; }
    public String getInstructions() { return instructions; }

    /**
     * Creates an immutable memento of current cart state.
     */
    public OrderMemento saveToMemento(String snapshotLabel) {
        System.out.println("[ORIGINATOR] Creating snapshot '" + snapshotLabel + "'...");
        return new OrderMemento(snapshotLabel, this.items, this.subtotal, this.couponCode, this.instructions);
    }

    /**
     * Restores state from a given memento.
     */
    public void restoreFromMemento(OrderMemento memento) {
        if (memento == null) return;
        this.items = memento.getCartItems();
        this.subtotal = memento.getSubtotal();
        this.couponCode = memento.getAppliedCoupon();
        this.instructions = memento.getInstructions();
        System.out.println("[ORIGINATOR] Restored cart from snapshot '" + memento.getSnapshotId() + "'! Items=" + items.size() + ", Subtotal=Rs. " + subtotal);
    }

    @Override
    public String toString() {
        return "Active Cart: Items=" + items + ", Subtotal=Rs. " + subtotal + ", Coupon=" + couponCode;
    }
}
