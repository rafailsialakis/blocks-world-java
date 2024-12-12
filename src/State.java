import java.util.*;

public class State implements Comparable<State> {
    private final ArrayList<Substack> state;
    private double g;  // Cost to reach this state (e.g., number of moves)
    private double h;  // Heuristic value (estimate to reach the goal)
    private double f;  // Total cost (f = g + h for A*)

    public State(Substack... substacks) {
        state = new ArrayList<Substack>();
        Collections.addAll(state, substacks);
    }

    public void printState()
    {
        for (Substack stack : state) {
            System.out.println(stack);
        }
        System.out.println();
    }

    public void printState(State goalState) {
        for (Substack stack : state) {
            System.out.println(stack);
        }

        // Calculate heuristic for the current state with respect to the goal state
        double misplacedBlocks = Problem.calculateHeuristic(this, goalState);
        System.out.println("Misplaced blocks: " + misplacedBlocks);
        System.out.println();
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setF(double f) {
        this.f = f;
    }

    public List<Substack> getState() {
        return this.state;
    }

    // Heuristic function to estimate the cost to the goal

    // Generate children for uninformed search (no heuristic)
    public List<State> generateChildrenUninformed() {
        List<State> children = new ArrayList<>();

        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);

            if (sourceStack.isEmpty()) {
                continue;
            }

            Block topBlock = sourceStack.peek();

            for (int j = 0; j < state.size(); j++) {
                if (i == j) {
                    continue;
                }

                List<Substack> newStacks = deepCopyStacks();
                newStacks.get(i).pop();
                newStacks.get(j).push(topBlock);

                State childState = new State(newStacks.toArray(new Substack[0]));
                childState.g = this.g + 1;  // Just the cost to reach the child (no heuristic)
                childState.f = childState.g; // No heuristic, so f = g for uninformed search

                children.add(childState);
            }
        }

        return children;
    }
    public String getMoveToChild(State child) {
        // Find the difference between the current state and the child state
        // Example: Iterate over the stacks and identify which block moved.

        for (int i = 0; i < this.state.size(); i++) {
            Substack currentStack = this.state.get(i);
            Substack childStack = child.state.get(i);

            // Check if the stacks are different
            if (!currentStack.equals(childStack)) {
                // Find the moved block (the one that is in the current stack but not in the child stack)
                Block movedBlock = findMovedBlock(currentStack, childStack);

                if (movedBlock != null) {
                    // Determine if the block was moved to another stack or placed on the table
                    if (childStack.contains(movedBlock)) {
                        // The block was moved to another stack
                        return "MOVE " + movedBlock.getName() + " " + getStackName(childStack);
                    } else {
                        // The block was placed on the table (stack is empty)
                        return "MOVE " + movedBlock.getName() + " table";
                    }
                }
            }
        }
        return "No valid move"; // If no move is found (this shouldn't happen in your problem)
    }
    public String getStackName(Substack stack) {
        // Iterate over the substacks and return the name of the stack by its index
        for (int i = 0; i < state.size(); i++) {
            if (state.get(i).equals(stack)) {
                return "Stack " + (i + 1);  // Name the stack as Stack 1, Stack 2, etc.
            }
        }
        return "Unknown Stack";  // If the stack is not found (should not happen)
    }

    // Helper method to find the moved block
    private Block findMovedBlock(Substack currentStack, Substack childStack) {
        for (Block block : currentStack.getBlocks()) {
            if (!childStack.getBlocks().contains(block)) {
                return block; // Block is missing in the child stack, so it has moved
            }
        }
        return null;
    }

    // Generate children for informed search (with heuristic)
    public List<State> generateChildrenInformed(State goalState) {
        List<State> children = new ArrayList<>();

        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);

            if (sourceStack.isEmpty()) {
                continue;
            }

            Block topBlock = sourceStack.peek();

            for (int j = 0; j < state.size(); j++) {
                if (i == j) {
                    continue;
                }

                List<Substack> newStacks = deepCopyStacks();
                newStacks.get(i).pop();
                newStacks.get(j).push(topBlock);

                State childState = new State(newStacks.toArray(new Substack[0]));
                childState.g = this.g + 1;  // Cost to reach the child (1 step more)
                childState.h = Problem.calculateHeuristic(childState, goalState);  // Recalculate heuristic for the child state
                childState.f = childState.g + childState.h;  // f = g + h

                children.add(childState);
            }
        }

        return children;
    }

    private List<Substack> deepCopyStacks() {
        List<Substack> copy = new ArrayList<>();
        for (Substack stack : state) {
            Substack newStack = new Substack();
            for (Block block : stack.stack) {
                newStack.push(block);
            }
            copy.add(newStack);
        }
        return copy;
    }

    @Override
    public int compareTo(State other) {
        return Double.compare(this.f, other.f); // For A*, use f = g + h
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        State otherState = (State) obj;

        // First, check if the sizes of the state lists are equal
        if (this.state.size() != otherState.state.size()) {
            return false;
        }

        // Now, compare the substacks in order
        for (int i = 0; i < this.state.size(); i++) {
            Substack substack1 = this.state.get(i);
            Substack substack2 = otherState.state.get(i);

            // Check if the two substacks are equal (order matters)
            if (!substack1.equals(substack2)) {
                return false;
            }
        }

        // If all checks pass, the states are equal
        return true;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
