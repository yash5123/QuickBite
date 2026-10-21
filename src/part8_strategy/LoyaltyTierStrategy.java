package part8_strategy;

/**
 * Concrete Strategy: VIP Loyalty Tier discount (e.g., 25% QuickBite Gold Member perk).
 */
public class LoyaltyTierStrategy implements DiscountStrategy {

    private final String membershipTier;
    private final double discountRate;

    public LoyaltyTierStrategy(String membershipTier, double discountRate) {
        this.membershipTier = membershipTier;
        this.discountRate = discountRate;
    }

    public LoyaltyTierStrategy() {
        this("QuickBite Gold VIP", 25.0);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        double discount = (subtotal * discountRate) / 100.0;
        System.out.println("[STRATEGY - LOYALTY] Applied " + membershipTier + " (" + discountRate + "% perk): Rs. " + discount);
        return discount;
    }

    @Override
    public String getStrategyName() {
        return membershipTier + " (" + discountRate + "% OFF)";
    }
}
