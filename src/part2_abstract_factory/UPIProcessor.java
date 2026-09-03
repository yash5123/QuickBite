package part2_abstract_factory;

public class UPIProcessor implements PaymentProcessor {
    @Override
    public void charge(double amount) {
        System.out.println("Charged Rs. " + amount + " via UPI.");
    }
}
