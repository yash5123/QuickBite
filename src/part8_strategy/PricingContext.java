package part8_strategy;

/**
 * Context class for the Strategy Pattern.
 * Holds a reference to the active DiscountStrategy and delegates pricing computations.
 */
public class PricingContext {

    private DiscountStrategy strategy;

    public PricingContext(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public PricingContext() {
        this(new NoDiscountStrategy());
    }

    public void setStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[STRATEGY] Swapped pricing algorithm to: " + strategy.getStrategyName());
    }

    public DiscountStrategy getStrategy() {
        return strategy;
    }

    public double computePayable(double subtotal) {
        double discount = strategy.calculateDiscount(subtotal);
        double finalAmount = Math.max(0.0, subtotal - discount);
        System.out.println("[STRATEGY] Subtotal: Rs. " + subtotal + " - Discount: Rs. " + discount + " = Payable: Rs. " + finalAmount);
        return finalAmount;
    }
}
