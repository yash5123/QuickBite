package part7_template_method;

/**
 * Concrete Class for the Template Method Pattern.
 * Implements packaging into express heat-chute containers and handover via drive-thru window.
 */
public class DriveThruFulfillmentWorkflow extends OrderFulfillmentWorkflow {

    @Override
    public String getWorkflowName() {
        return "Drive-Thru Express Window Workflow";
    }

    @Override
    protected void packageOrder() {
        System.out.println("[WORKFLOW - DRIVE-THRU] 4. Sealed in rapid-serve thermal heat-chute container with auto-dispensed condiments.");
    }

    @Override
    protected void handoverOrder() {
        System.out.println("[WORKFLOW - DRIVE-THRU] 5. Passed through Window #2 contactless lane; vehicle barcode sensor verified handoff.");
    }
}
