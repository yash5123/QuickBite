package part8_strategy;

/**
 * Concrete Strategy: No discount applied (Regular baseline pricing).
 */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal) {
        System.out.println("[STRATEGY - NO DISCOUNT] Standard menu price applied.");
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "No Discount (Standard Pricing)";
    }
}
