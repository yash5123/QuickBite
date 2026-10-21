package part7_template_method;

/**
 * Concrete Class for the Template Method Pattern.
 * Implements packaging into branded eco-paper bags and handover via express pickup counter.
 */
public class TakeawayFulfillmentWorkflow extends OrderFulfillmentWorkflow {

    @Override
    public String getWorkflowName() {
        return "Customer Takeaway / Pickup Workflow";
    }

    @Override
    protected void packageOrder() {
        System.out.println("[WORKFLOW - TAKEAWAY] 4. Packed into recyclable brown kraft carry-bag with cutlery and tissue set.");
    }

    @Override
    protected void handoverOrder() {
        System.out.println("[WORKFLOW - TAKEAWAY] 5. Placed on Counter Shelf #3; pickup buzzer ticket display updated.");
    }
}
