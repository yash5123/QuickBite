package part7_template_method;

/**
 * Concrete Class for the Template Method Pattern.
 * Implements packaging via ceramic dishware and table delivery service.
 */
public class DineInFulfillmentWorkflow extends OrderFulfillmentWorkflow {

    @Override
    public String getWorkflowName() {
        return "Dine-In Table Service Workflow";
    }

    @Override
    protected void packageOrder() {
        System.out.println("[WORKFLOW - DINE-IN] 4. Plated onto artisanal ceramic dinnerware with parsley garnish.");
    }

    @Override
    protected void handoverOrder() {
        System.out.println("[WORKFLOW - DINE-IN] 5. Server delivered piping-hot tray directly to Table #14 with water refill.");
    }
}
