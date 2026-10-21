package part6_builder_prototype;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for the Builder Pattern.
 * Provides a fluent interface to construct complex CustomOrder objects step-by-step.
 */
public class CustomOrderBuilder {

    private String customerName = "Guest Customer";
    private String baseDish = "Classic Burger";
    private String size = "Regular";
    private List<String> toppings = new ArrayList<>();
    private String sideDrink = "None";
    private String spiceLevel = "Medium";
    private String specialInstructions = "None";
    private double basePrice = 250.0;

    public CustomOrderBuilder setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public CustomOrderBuilder setBaseDish(String baseDish, double basePrice) {
        this.baseDish = baseDish;
        this.basePrice = basePrice;
        return this;
    }

    public CustomOrderBuilder setSize(String size) {
        this.size = size;
        return this;
    }

    public CustomOrderBuilder addTopping(String topping) {
        this.toppings.add(topping);
        return this;
    }

    public CustomOrderBuilder setToppings(List<String> toppings) {
        this.toppings = new ArrayList<>(toppings);
        return this;
    }

    public CustomOrderBuilder setSideDrink(String sideDrink) {
        this.sideDrink = sideDrink;
        return this;
    }

    public CustomOrderBuilder setSpiceLevel(String spiceLevel) {
        this.spiceLevel = spiceLevel;
        return this;
    }

    public CustomOrderBuilder setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        return this;
    }

    public String getCustomerName() { return customerName; }
    public String getBaseDish() { return baseDish; }
    public String getSize() { return size; }
    public List<String> getToppings() { return toppings; }
    public String getSideDrink() { return sideDrink; }
    public String getSpiceLevel() { return spiceLevel; }
    public String getSpecialInstructions() { return specialInstructions; }
    public double getBasePrice() { return basePrice; }

    public CustomOrder build() {
        System.out.println("[BUILDER] Assembling custom order for " + customerName + ": " + baseDish + " (" + size + ")");
        return new CustomOrder(this);
    }
}
