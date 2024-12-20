import java.util.HashSet;
import java.util.Stack;

public class Substack {
    Stack<Block> stack;

    // Constructor to initialize an empty stack
    public Substack() {
        stack = new Stack<>();
    }

    // Constructor to create a copy of another Substack
    public Substack(Substack other) {
        this.stack = new Stack<>();
        this.stack.addAll(other.getBlocks());
    }

    // Returns the size of the stack
    public int size() {
        return stack.size();
    }

    // Removes the top element of the stack if it is not empty
    public void pop() {
        if (!stack.isEmpty()){
            stack.pop();
        }
    }

    // Retrieves the block at the specified index in the stack
    public Block getBlockAtIndex(int i) {
        return stack.get(i);
    }

    // Finds the index of a given block in the stack
    public int getIndexByElement(Block b) {
        return stack.indexOf(b);
    }

    // Checks if the stack is empty
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Provides a string representation of the stack
    @Override
    public String toString() {
        return stack.toString();  // This prints the list of blocks in the stack.
    }

    // Returns the top block of the stack without removing it
    public Block peek() {
        return stack.peek();
    }

    // Adds a block to the top of the stack
    public void push(Block topBlock) {
        stack.push(topBlock);
    }

    // Checks equality by comparing the stacks of two Substack objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Substack otherStack = (Substack) obj;
        return stack.equals(otherStack.stack);  // Compare lists in the exact order
    }

    // Generates a hash code based on the set of blocks in the stack
    @Override
    public int hashCode() {
        return new HashSet<>(stack).hashCode();
    }

    // Retrieves the entire stack of blocks
    public Stack<Block> getBlocks() {
        return stack;
    }
}
