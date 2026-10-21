package part8_strategy;

/**
 * Concrete Strategy: Flat reduction (e.g., Flat Rs. 50 voucher).
 */
public class FlatDiscountStrategy implements DiscountStrategy {

    private final double flatAmount;

    public FlatDiscountStrategy(double flatAmount) {
        this.flatAmount = flatAmount;
    }

    public FlatDiscountStrategy() {
        this(50.0);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        double discount = Math.min(flatAmount, subtotal);
        System.out.println("[STRATEGY - FLAT] Deducted flat Rs. " + discount + " from subtotal Rs. " + subtotal);
        return discount;
    }

    @Override
    public String getStrategyName() {
        return "Flat Voucher Discount (Rs. " + flatAmount + " OFF)";
    }
}
