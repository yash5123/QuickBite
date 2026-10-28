package part8_strategy;

/**
 * Concrete Strategy: Threshold-based bulk order discount.
 * Applies a rebate only if subtotal meets or exceeds a target threshold.
 */
public class ThresholdDiscountStrategy implements DiscountStrategy {

    private final double threshold;
    private final double discountAmount;

    public ThresholdDiscountStrategy(double threshold, double discountAmount) {
        this.threshold = threshold;
        this.discountAmount = discountAmount;
    }

    public ThresholdDiscountStrategy() {
        this(500.0, 120.0);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        if (subtotal >= threshold) {
            double discount = Math.min(discountAmount, subtotal);
            System.out.println("[STRATEGY - THRESHOLD] Subtotal Rs. " + subtotal + " qualifies for Rs. " + threshold + "+ threshold: Deducted Rs. " + discount);
            return discount;
        } else {
            System.out.println("[STRATEGY - THRESHOLD] Subtotal Rs. " + subtotal + " did not meet Rs. " + threshold + " minimum threshold. No discount applied.");
            return 0.0;
        }
    }

    @Override
    public String getStrategyName() {
        return "Threshold Rebate (Rs. " + discountAmount + " OFF over Rs. " + threshold + ")";
    }

    public double getThreshold() {
        return threshold;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}

