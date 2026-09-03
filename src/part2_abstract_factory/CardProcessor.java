package part2_abstract_factory;

public class CardProcessor implements PaymentProcessor {
    @Override
    public void charge(double amount) {
        System.out.println("Charged $" + amount + " via Card.");
    }
}
