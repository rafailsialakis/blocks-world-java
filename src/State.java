import java.util.*;

public class State implements Comparable<State> {
    private final ArrayList<Substack> state;
    private static int countSubstacks;
    private double g;  // Cost to reach this state (e.g., number of moves)
    private double h;  // Heuristic value (estimate to reach the goal)
    private double f;  // Total cost (f = g + h for A*)

    public State(Substack... substacks) {
        state = new ArrayList<Substack>();
        Collections.addAll(state, substacks);
        countSubstacks = 0;
    }

    public void addStack(Substack e){
        state.add(e);
        countSubstacks++;
    }

    public void addStacksToInitialize(ArrayList<Block> blocks){
        for(int i = countSubstacks; i < blocks.size(); i++){
            this.addStack(new Substack());
        }
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

    public double getF() {
        return f;
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
        return state;
    }

    // Heuristic function to estimate the cost to the goal

    // Generate children for uninformed search (no heuristic)
    public List<State> generateChildrenUninformed(Set<State> visited) {
        List<State> children = new ArrayList<>();

        // Iterate through each stack in the current state
        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);
            if (sourceStack.isEmpty()) continue; // Skip empty stacks

            Block topBlock = sourceStack.peek(); // Get the top block

            // Try moving the block to each of the other stacks
            for (int j = 0; j < state.size(); j++) {
                if (i == j) continue; // Skip moving to the same stack

                // Deep copy the stacks to simulate the move
                List<Substack> newStacks = deepCopyStacks();
                newStacks.get(i).pop();
                newStacks.get(j).push(topBlock);

                // Create a new state from the modified stacks
                State childState = new State(newStacks.toArray(new Substack[0]));

                // Add to children (do not update visited here)
                if (!visited.contains(childState)) {
                    children.add(childState);
                }
            }
        }
        return children;
    }

    // Generate children for informed search (with heuristic)
    public List<State> generateChildrenInformed(State goal, Set<State> visited) {
        List<State> children = new ArrayList<>();

        // Iterate through each stack in the current state
        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);
            if (sourceStack.isEmpty()) continue; // Skip empty stacks

            Block topBlock = sourceStack.peek(); // Get the top block

            // Try moving the block to each of the other stacks
            for (int j = 0; j < state.size(); j++) {
                if (i == j) continue; // Skip moving to the same stack

                // Deep copy the stacks to simulate the move
                List<Substack> newStacks = deepCopyStacks();
                newStacks.get(i).pop();
                newStacks.get(j).push(topBlock);

                // Create a new state from the modified stacks
                State childState = new State(newStacks.toArray(new Substack[0]));

                // Compute heuristic for A* or Best-First
                childState.setH(Problem.calculateHeuristic(childState, goal));

                // Add child only if not already visited
                if (!visited.contains(childState)) {
                    children.add(childState);
                }
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
