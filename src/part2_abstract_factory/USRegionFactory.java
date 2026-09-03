package part2_abstract_factory;

// S_3
public class USRegionFactory implements RegionFactory {
    @Override
    public TaxCalculator createTaxCalculator() {
        return new SalesTaxCalculator();
    }

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new CardProcessor();
    }
}
