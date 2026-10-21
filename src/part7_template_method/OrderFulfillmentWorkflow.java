package part7_template_method;

/**
 * Abstract Class for the Template Method Pattern.
 * Defines the invariant skeleton of the order fulfillment workflow.
 * Subclasses specialize the packaging and handover steps without altering the algorithm structure.
 */
public abstract class OrderFulfillmentWorkflow {

    /**
     * The Template Method. Marked final so subclasses cannot break the sequence.
     */
    public final void fulfillOrder(String orderId, String customer, String item, double amount) {
        System.out.println("\n--- [TEMPLATE METHOD] Starting " + getWorkflowName() + " for #" + orderId + " ---");
        verifyOrder(orderId, customer, amount);
        prepareFood(item);
        qualityCheck();
        packageOrder();     // Abstract step (specialized by subclasses)
        handoverOrder();    // Abstract step (specialized by subclasses)
        sendCompletionAlert(customer);
        System.out.println("--- [TEMPLATE METHOD] Workflow finished for #" + orderId + " ---");
    }

    public abstract String getWorkflowName();

    protected void verifyOrder(String orderId, String customer, double amount) {
        System.out.println("[WORKFLOW] 1. Verified order #" + orderId + " for " + customer + " (Total: Rs. " + amount + ")");
    }

    protected void prepareFood(String item) {
        System.out.println("[WORKFLOW] 2. Kitchen team cooked and assembled: " + item);
    }

    protected void qualityCheck() {
        System.out.println("[WORKFLOW] 3. Head Chef passed temperature and presentation inspection.");
    }

    // Primitive hook operations to be customized
    protected abstract void packageOrder();
    protected abstract void handoverOrder();

    protected void sendCompletionAlert(String customer) {
        System.out.println("[WORKFLOW] 6. Customer " + customer + " notified of successful fulfillment!");
    }
}
