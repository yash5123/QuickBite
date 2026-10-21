package part8_strategy;

/**
 * Concrete Strategy: Percentage promotional discount (e.g., 15% festival promo).
 */
public class PercentageDiscountStrategy implements DiscountStrategy {

    private final double percent; // e.g. 15 for 15%

    public PercentageDiscountStrategy(double percent) {
        this.percent = percent;
    }

    public PercentageDiscountStrategy() {
        this(15.0);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        double discount = (subtotal * percent) / 100.0;
        System.out.println("[STRATEGY - PERCENTAGE] Deducted " + percent + "% promo discount: Rs. " + discount);
        return discount;
    }

    @Override
    public String getStrategyName() {
        return "Promotional " + percent + "% Discount";
    }
}
