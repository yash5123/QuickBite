package part6_builder_prototype;

/**
 * Director class for the Builder Pattern.
 * Defines standard construction sequences for predefined popular meal combos.
 */
public class MealDirector {

    public CustomOrder constructBurgerCombo(String customerName) {
        System.out.println("[DIRECTOR] Constructing 'Signature Burger Combo'...");
        return new CustomOrderBuilder()
                .setCustomerName(customerName)
                .setBaseDish("Gourmet Truffle Burger", 320.0)
                .setSize("Medium")
                .addTopping("Cheddar Cheese")
                .addTopping("Caramelized Onions")
                .setSideDrink("Iced Lemon Tea")
                .setSpiceLevel("Mild")
                .setSpecialInstructions("Extra napkins")
                .build();
    }

    public CustomOrder constructFitnessMeal(String customerName) {
        System.out.println("[DIRECTOR] Constructing 'Keto Fitness Bowl'...");
        return new CustomOrderBuilder()
                .setCustomerName(customerName)
                .setBaseDish("Grilled Chicken Quinoa Bowl", 380.0)
                .setSize("Large")
                .addTopping("Avocado")
                .addTopping("Boiled Egg")
                .addTopping("Chia Seeds")
                .setSideDrink("Fresh Cold-Pressed Juice")
                .setSpiceLevel("Low")
                .setSpecialInstructions("Dressing on the side, zero sugar")
                .build();
    }

    public CustomOrder constructPartyFeast(String customerName) {
        System.out.println("[DIRECTOR] Constructing 'Ultimate Party Feast'...");
        return new CustomOrderBuilder()
                .setCustomerName(customerName)
                .setBaseDish("Double Pepperoni & Loaded Cheese Pizza", 550.0)
                .setSize("Extra-Large")
                .addTopping("Extra Mozzarella")
                .addTopping("Jalapenos")
                .addTopping("Smoked BBQ Dip")
                .setSideDrink("2L Pitcher Soda")
                .setSpiceLevel("Hot & Spicy")
                .setSpecialInstructions("Pre-sliced into 12 slices")
                .build();
    }
}
