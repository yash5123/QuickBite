package part6_builder_prototype;

import java.util.ArrayList;
import java.util.List;

/**
 * Product class for the Builder Pattern.
 * Represents a rich, customizable food order with multiple optional components.
 */
public class CustomOrder {

    private final String customerName;
    private final String baseDish;
    private final String size;
    private final List<String> toppings;
    private final String sideDrink;
    private final String spiceLevel;
    private final String specialInstructions;
    private final double basePrice;

    public CustomOrder(CustomOrderBuilder builder) {
        this.customerName = builder.getCustomerName();
        this.baseDish = builder.getBaseDish();
        this.size = builder.getSize();
        this.toppings = new ArrayList<>(builder.getToppings());
        this.sideDrink = builder.getSideDrink();
        this.spiceLevel = builder.getSpiceLevel();
        this.specialInstructions = builder.getSpecialInstructions();
        this.basePrice = builder.getBasePrice();
    }

    public String getCustomerName() { return customerName; }
    public String getBaseDish() { return baseDish; }
    public String getSize() { return size; }
    public List<String> getToppings() { return new ArrayList<>(toppings); }
    public String getSideDrink() { return sideDrink; }
    public String getSpiceLevel() { return spiceLevel; }
    public String getSpecialInstructions() { return specialInstructions; }
    public double getBasePrice() { return basePrice; }

    public double calculateSubtotal() {
        double total = basePrice;
        total += toppings.size() * 30.0; // 30 per extra topping
        if (sideDrink != null && !sideDrink.equalsIgnoreCase("None") && !sideDrink.isEmpty()) {
            total += 45.0; // Drink surcharge
        }
        return total;
    }

    @Override
    public String toString() {
        return "CustomOrder {" +
                "customer='" + customerName + '\'' +
                ", dish='" + baseDish + " (" + size + ")'" +
                ", toppings=" + toppings +
                ", drink='" + sideDrink + '\'' +
                ", spice='" + spiceLevel + '\'' +
                ", notes='" + specialInstructions + '\'' +
                ", subtotal=" + calculateSubtotal() +
                '}';
    }
}
