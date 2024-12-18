import java.util.HashSet;
import java.util.Stack;

public class Substack {
    Stack<Block> stack;
    public Substack() {
        stack = new Stack<>();
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
    public Block getBlockAtIndex(int i){
        return stack.get(i);
    }

    public int getIndexByElement(Block b){
        return stack.indexOf(b);
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

    public Stack<Block> getBlocks() {
        return stack;
    }
}
