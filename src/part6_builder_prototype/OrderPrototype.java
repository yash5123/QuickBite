package part6_builder_prototype;

/**
 * Prototype interface for the Prototype Pattern.
 * Declares the cloning method for reordering existing meal tickets.
 */
public interface OrderPrototype {
    OrderPrototype cloneOrder();
}
