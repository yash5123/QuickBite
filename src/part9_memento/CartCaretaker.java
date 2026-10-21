package part9_memento;

import java.util.Stack;

/**
 * Caretaker class for the Memento Pattern.
 * Manages the stack of OrderMemento snapshots without inspecting or modifying their contents.
 */
public class CartCaretaker {

    private final Stack<OrderMemento> history = new Stack<>();

    public void backup(CartOriginator originator, String label) {
        OrderMemento memento = originator.saveToMemento(label);
        history.push(memento);
        System.out.println("[CARETAKER] Snapshot stored in undo history stack. Total snapshots: " + history.size());
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void undo(CartOriginator originator) {
        if (!history.isEmpty()) {
            OrderMemento previousState = history.pop();
            System.out.println("[CARETAKER] Popping snapshot from undo stack: '" + previousState.getSnapshotId() + "'");
            originator.restoreFromMemento(previousState);
        } else {
            System.out.println("[CARETAKER] Undo stack is empty. Nothing to restore.");
        }
    }

    public int getHistorySize() {
        return history.size();
    }
}
