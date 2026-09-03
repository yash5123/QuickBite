package part2_abstract_factory;

public class GSTCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.05; // 5% GST
    }
}
