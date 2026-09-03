package part2_abstract_factory;

// S_2
public class IndiaRegionFactory implements RegionFactory {
    @Override
    public TaxCalculator createTaxCalculator() {
        return new GSTCalculator();
    }

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new UPIProcessor();
    }
}
