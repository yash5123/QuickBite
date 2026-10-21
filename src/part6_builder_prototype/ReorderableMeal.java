package part6_builder_prototype;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Prototype class.
 * Models a saved meal ticket that can be cloned with deep-copy independence
 * to power QuickBite's "1-Click Reorder" feature.
 */
public class ReorderableMeal implements OrderPrototype {

    private String originalOrderId;
    private String customerName;
    private String mealName;
    private double price;
    private List<String> addons;
    private String deliveryAddress;

    public ReorderableMeal(String originalOrderId, String customerName, String mealName, double price, List<String> addons, String deliveryAddress) {
        this.originalOrderId = originalOrderId;
        this.customerName = customerName;
        this.mealName = mealName;
        this.price = price;
        this.addons = new ArrayList<>(addons);
        this.deliveryAddress = deliveryAddress;
    }

    // Copy constructor for deep cloning
    public ReorderableMeal(ReorderableMeal target) {
        if (target != null) {
            this.originalOrderId = target.originalOrderId;
            this.customerName = target.customerName;
            this.mealName = target.mealName;
            this.price = target.price;
            this.addons = new ArrayList<>(target.addons); // Deep copy of list
            this.deliveryAddress = target.deliveryAddress;
        }
    }

    @Override
    public ReorderableMeal cloneOrder() {
        System.out.println("[PROTOTYPE] Cloning saved meal ticket #" + originalOrderId + " for " + customerName);
        return new ReorderableMeal(this);
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void addAddon(String addon) {
        this.addons.add(addon);
    }

    public String getOriginalOrderId() { return originalOrderId; }
    public String getCustomerName() { return customerName; }
    public String getMealName() { return mealName; }
    public double getPrice() { return price; }
    public List<String> getAddons() { return new ArrayList<>(addons); }
    public String getDeliveryAddress() { return deliveryAddress; }

    @Override
    public String toString() {
        return "ReorderableMeal {" +
                "templateId='" + originalOrderId + '\'' +
                ", customer='" + customerName + '\'' +
                ", meal='" + mealName + '\'' +
                ", price=" + price +
                ", addons=" + addons +
                ", address='" + deliveryAddress + '\'' +
                '}';
    }
}
