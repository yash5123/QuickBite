package part3_chain_of_responsibility;

import part1_singleton.Order;

// G_1
public abstract class OrderHandler {

    protected OrderHandler next;

    public void setNext(OrderHandler next) {
        this.next = next;
    }

    public abstract boolean handle(Order order);
}
