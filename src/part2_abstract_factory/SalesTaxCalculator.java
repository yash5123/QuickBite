package part2_abstract_factory;

public class SalesTaxCalculator implements TaxCalculator {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.08; // 8% US sales tax
    }
}
