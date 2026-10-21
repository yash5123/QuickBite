package part7_template_method;

/**
 * Concrete Class for the Template Method Pattern.
 * Implements packaging with thermal insulation and handover via GPS rider fleet.
 */
public class DeliveryFulfillmentWorkflow extends OrderFulfillmentWorkflow {

    @Override
    public String getWorkflowName() {
        return "Home Delivery Fulfillment Workflow";
    }

    @Override
    protected void packageOrder() {
        System.out.println("[WORKFLOW - DELIVERY] 4. Sealed in tamper-evident hot/cold thermal insulated pouch with ice-pack.");
    }

    @Override
    protected void handoverOrder() {
        System.out.println("[WORKFLOW - DELIVERY] 5. Dispatched to QuickBite Delivery Partner (Fleet Unit #7, Live GPS tracking enabled).");
    }
}
