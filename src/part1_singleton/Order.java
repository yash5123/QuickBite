package part1_singleton;

import part2_abstract_factory.PaymentProcessor;

// P_1
public class Order {

    private String customerName;
    private double amount;
    private double tax;
    private String couponCode;
    private PaymentProcessor paymentProcessor;

    public Order(String customerName, double amount, String couponCode) {
        this.customerName = customerName;
        this.amount = amount;
        this.couponCode = couponCode;
    }

    public String getCustomerName() { return customerName; }
    public double getAmount() { return amount; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public String getCouponCode() { return couponCode; }
    public PaymentProcessor getPaymentProcessor() { return paymentProcessor; }
    public void setPaymentProcessor(PaymentProcessor paymentProcessor) { this.paymentProcessor = paymentProcessor; }
    public double getTotalPayable() { return amount + tax; }
}
