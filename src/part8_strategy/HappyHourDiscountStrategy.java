package part8_strategy;

/**
 * Concrete Strategy: Flash Happy Hour promotional discount with a maximum cap.
 */
public class HappyHourDiscountStrategy implements DiscountStrategy {

    private final double percentage;
    private final double maxCap;

    public HappyHourDiscountStrategy(double percentage, double maxCap) {
        this.percentage = percentage;
        this.maxCap = maxCap;
    }

    public HappyHourDiscountStrategy() {
        this(20.0, 150.0);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        double rawDiscount = (subtotal * percentage) / 100.0;
        double discount = Math.min(rawDiscount, maxCap);
        System.out.println("[STRATEGY - HAPPY HOUR] Flash " + percentage + "% discount capped at Rs. " + maxCap + ": Deducted Rs. " + discount);
        return discount;
    }

    @Override
    public String getStrategyName() {
        return "Happy Hour Flash (" + percentage + "% OFF up to Rs. " + maxCap + ")";
    }

    public double getPercentage() {
        return percentage;
    }

    public double getMaxCap() {
        return maxCap;
    }
}
