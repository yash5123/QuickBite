package part6_builder_prototype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Registry / Manager.
 * Stores prototypical meal tickets and creates fresh clones on request.
 * Uses static prototype caching so custom saved prototypes persist across orders.
 */
public class OrderRegistry {

    private static final Map<String, ReorderableMeal> prototypes = new HashMap<>();

    static {
        loadDefaultPrototypes();
    }

    public OrderRegistry() {
        // Retained for backward compatibility
    }

    private static void loadDefaultPrototypes() {
        ReorderableMeal burgerCombo = new ReorderableMeal(
                "ORD-701",
                "Chef Signature",
                "Gourmet Brioche Burger (Medium)",
                350.0,
                Arrays.asList("Fresh Tomatoes", "Smoked Cheddar", "Caramelized Onions"),
                "QuickBite Kitchen Counter"
        );

        ReorderableMeal weeklyLunch = new ReorderableMeal(
                "ORD-901",
                "Rahul Verma",
                "Executive Thali Meal (Regular)",
                299.0,
                Arrays.asList("Extra Gulab Jamun", "Roasted Papad"),
                "Tech Park, Tower B, 4th Floor"
        );

        ReorderableMeal weekendPizza = new ReorderableMeal(
                "ORD-902",
                "Emily Davis",
                "Margherita Thin Crust (Large)",
                420.0,
                Arrays.asList("Garlic Breadsticks", "Chili Flakes"),
                "742 Evergreen Terrace"
        );

        prototypes.put("BURGER_COMBO", burgerCombo);
        prototypes.put("WEEKLY_LUNCH", weeklyLunch);
        prototypes.put("WEEKEND_PIZZA", weekendPizza);
    }

    public static void registerStaticPrototype(String key, ReorderableMeal meal) {
        prototypes.put(key, meal);
    }

    public void registerPrototype(String key, ReorderableMeal meal) {
        registerStaticPrototype(key, meal);
    }

    public static ReorderableMeal getStaticClone(String key) {
        ReorderableMeal prototype = prototypes.get(key);
        if (prototype != null) {
            return prototype.cloneOrder();
        }
        System.out.println("[PROTOTYPE] No saved template found for key: " + key);
        return null;
    }

    public ReorderableMeal getClone(String key) {
        return getStaticClone(key);
    }

    public static Map<String, ReorderableMeal> getAllPrototypes() {
        return Collections.unmodifiableMap(prototypes);
    }

    public static boolean containsPrototype(String key) {
        return prototypes.containsKey(key);
    }
}
