package part8_strategy;

/**
 * Strategy Interface for the Strategy Pattern.
 * Defines a family of interchangeable discount computation algorithms.
 */
public interface DiscountStrategy {
    double calculateDiscount(double subtotal);
    String getStrategyName();
}
