package part6_builder_prototype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Registry / Manager.
 * Stores prototypical meal tickets and creates fresh clones on request.
 */
public class OrderRegistry {

    private final Map<String, ReorderableMeal> prototypes = new HashMap<>();

    public OrderRegistry() {
        loadPrototypes();
    }

    private void loadPrototypes() {
        ReorderableMeal weeklyLunch = new ReorderableMeal(
                "ORD-901",
                "Rahul Verma",
                "Executive Thali Meal",
                299.0,
                Arrays.asList("Extra Gulab Jamun", "Roasted Papad"),
                "Tech Park, Tower B, 4th Floor"
        );

        ReorderableMeal weekendPizza = new ReorderableMeal(
                "ORD-902",
                "Emily Davis",
                "Margherita Thin Crust",
                420.0,
                Arrays.asList("Garlic Breadsticks", "Chili Flakes"),
                "742 Evergreen Terrace"
        );

        prototypes.put("WEEKLY_LUNCH", weeklyLunch);
        prototypes.put("WEEKEND_PIZZA", weekendPizza);
    }

    public void registerPrototype(String key, ReorderableMeal meal) {
        prototypes.put(key, meal);
    }

    public ReorderableMeal getClone(String key) {
        ReorderableMeal prototype = prototypes.get(key);
        if (prototype != null) {
            return prototype.cloneOrder();
        }
        System.out.println("[PROTOTYPE] No saved template found for key: " + key);
        return null;
    }
}
