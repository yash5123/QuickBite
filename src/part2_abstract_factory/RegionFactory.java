package part2_abstract_factory;

// S_1
public interface RegionFactory {
    TaxCalculator createTaxCalculator();
    PaymentProcessor createPaymentProcessor();
}
