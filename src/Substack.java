import java.util.HashSet;
import java.util.Stack;

public class Substack {
    Stack<Block> stack;
    public Substack(Block... blocks) {
        stack = new Stack<Block>();
        for (Block block : blocks) {
            stack.push(block);
        }
    }
    public void printStack()
    {
        for (Block block: stack)
        {
            System.out.println(block);
        }
    }

    public int size()
    {
        return stack.size();
    }


    public void pop()
    {
        if (!stack.isEmpty()){
            stack.pop();
        }
    }

    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    @Override
    public String toString() {
        return stack.toString();  // This prints the list of blocks in the stack.
    }


    public Block peek() {
        return stack.peek();
    }

    public void push(Block topBlock) {
        stack.push(topBlock);
    }

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


    @Override
    public int hashCode() {
        // Generate a hash based on the set of blocks
        return new HashSet<>(stack).hashCode();
    }

    public Block getBlockAt(int j) {
        return stack.get(j);
    }

    public Stack<Block> getBlocks() {
        return stack;
    }

    // Contains method to check if a Block is in this Substack
    public boolean contains(Block block) {
        return stack.contains(block);
    }

    public Stack<Block> getStack() {
        return stack;
    }
}
