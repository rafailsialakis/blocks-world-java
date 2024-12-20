import java.util.*;

public class State implements Comparable<State> {
    private State Parent; // Reference to the parent state (used for backtracking in pathfinding)
    private final ArrayList<Substack> state; // Represents the current configuration of stacks
    private int g;  // Cost to reach this state (e.g., number of moves from the initial state)
    private int h;  // Heuristic value (estimate of cost to reach the goal)
    private int f;  // Total cost (f = g + h), used in A* search

    // Constructor to initialize the state with a variable number of substacks
    public State(Substack... substacks) {
        state = new ArrayList<>();
        Collections.addAll(state, substacks);
        Parent = null; // Default to no parent
    }

    // Constructor to initialize the state with a list of substacks
    public State(List<Substack> tempState1) {
        state = new ArrayList<>(tempState1); // Create a copy of the provided list
        Parent = null; // Default to no parent
    }

    // Sets the parent state (for backtracking or path reconstruction)
    public void setParent(State parent) {
        Parent = parent;
    }

    // Adds a substack to the current state
    public void addStack(Substack e) {
        state.add(e);
    }

    // Finds the largest substack in the current state (by size)
    public Substack findLargestSubstack() {
        Substack largestSubstack = this.getState().getFirst(); // Start with the first substack
        for (Substack substack : this.getState()) {
            if (substack.size() > largestSubstack.size()) {
                largestSubstack = substack; // Update if a larger substack is found
            }
        }
        return largestSubstack;
    }

    // Prints the current state's substacks
    public void printState() {
        for (Substack stack : state) {
            System.out.println(stack);
        }
        System.out.println();
    }

    // Prints the current state along with its heuristic cost
    public void printState(State goalState, String algorithm) {
        for (Substack stack : state) {
            System.out.println(stack);
        }
        int misplacedBlocks = Problem.Heuristic(this, goalState); // Calculate heuristic value
        if (algorithm.equals("best")) {
            System.out.println("Cost of node: " + misplacedBlocks);
        } else {
            System.out.println("Cost of node: " + (misplacedBlocks + this.getG()));
        }
        System.out.println();
    }

    // Removes empty substacks and returns the current state
    public State filterState() {
        List<Substack> state = this.getState();
        state.removeIf(Substack::isEmpty);
        return this;
    }

    // Getters and setters for cost values
    public int getG() { return g; }
    public int getH() { return h; }
    public int getF() { return f; }
    public void setG(int g) { this.g = g; }
    public void setH(int h) { this.h = h; }
    public void setF(int f) { this.f = f; }

    // Returns the list of substacks representing the state
    public List<Substack> getState() { return state; }

    /*
    Generates the child states for the current state using an uninformed search strategy (such as Breadth-First Search or Depth-First Search).
    The method creates new states by moving blocks from one stack to another. The generated child states are not revisited, ensuring the
    exploration of new configurations. The parent-child relationship is established for backtracking purposes, but no heuristic or path cost
    is calculated, making it suitable for uninformed search approaches.

    Parameters:
    - visited: A set of already visited states to prevent generating duplicate states.

    Returns:
    - A list of child states, each with an established parent reference for backtracking.
    */
    public List<State> generateChildrenUninformed(Set<State> visited) {
        State.filterChild(this); // Remove empty substacks and ensure at least one empty stack
        List<State> children = new ArrayList<>();
        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);
            if (sourceStack.isEmpty()) continue; // Skip empty stacks
            Block topBlock = sourceStack.peek(); // Get the top block

            // Try moving the top block to other stacks
            for (int j = 0; j < state.size(); j++) {
                if (i == j) continue; // Skip moving to the same stack
                List<Substack> newStacks = deepCopyStacks(); // Create a copy of the stacks
                newStacks.get(i).pop(); // Remove the block from the source stack
                newStacks.get(j).push(topBlock); // Add the block to the target stack

                State childState = new State(newStacks); // Create a new state
                if (!visited.contains(childState)) {
                    childState.setParent(this); // Set the parent for backtracking
                    children.add(childState); // Add to the list of children
                }
            }
        }
        return children;
    }

    // Ensures the state has a single empty stack (filter out extra empty stacks)
    public static void filterChild(State s) {
        List<Substack> state = s.getState();
        state.removeIf(Substack::isEmpty);
        state.add(new Substack());
    }

    /*
    Generates the child states for the current state using an informed search strategy (such as A*).
    The method creates new states by moving blocks from one stack to another, calculating the heuristic
    for each resulting state, and updating the path cost (g-value). The method ensures that the generated
    states are not in the visited set to avoid revisiting states. It also computes the f-value (g + h) for each child.

    Parameters:
    - goal: The goal state used for calculating the heuristic.
    - visited: A set of already visited states to prevent generating duplicate states.

    Returns:
    - A list of child states, each with updated g, h, and f values.
    */
    public List<State> generateChildrenInformed(State goal, Set<State> visited) {
        State.filterChild(this);
        List<State> children = new ArrayList<>();
        for (int i = 0; i < state.size(); i++) {
            Substack sourceStack = state.get(i);
            if (sourceStack.isEmpty()) continue;
            Block topBlock = sourceStack.peek();

            for (int j = 0; j < state.size(); j++) {
                if (i == j) continue;
                List<Substack> newStacks = deepCopyStacks();
                newStacks.get(i).pop();
                newStacks.get(j).push(topBlock);

                State childState = new State(newStacks);
                if (!visited.contains(childState)) {
                    childState.setParent(this);
                    childState.setH(Problem.Heuristic(childState, goal)); // Set heuristic value
                    childState.setG(this.g + 1); // Increment path cost
                    childState.setF(childState.getG() + childState.getH()); // Compute total cost
                    children.add(childState);
                }
            }
        }
        return children;
    }

    // Creates a deep copy of the current stacks
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

    // Comparator for priority queue (based on total cost `f`)
    @Override
    public int compareTo(State other) {
        return Double.compare(this.f, other.f);
    }

    // Equality check based on normalized state
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        State otherState = (State) obj;
        List<Substack> thisNormalized = normalizeState(this.state);
        List<Substack> otherNormalized = normalizeState(otherState.state);

        return thisNormalized.equals(otherNormalized);
    }

    // Hash code computation based on normalized state
    @Override
    public int hashCode() {
        return normalizeState(this.state).hashCode();
    }

    /*
     Normalizes the given list of stacks by removing any empty stacks and sorting the remaining stacks.
     The sorting is done based on the string representation of each stack, ensuring a consistent order for comparison
     or further processing. This is useful for state comparisons or ensuring a canonical form of the state.

     Parameters:
     - stacks: The list of stacks to be normalized.

     Returns:
     - A new list of normalized stacks, with empty stacks removed and the remaining stacks sorted.
     */
    private List<Substack> normalizeState(List<Substack> stacks) {
        List<Substack> normalized = new ArrayList<>(stacks);
        normalized.removeIf(Substack::isEmpty);
        normalized.sort(Comparator.comparing(Object::toString));
        return normalized;
    }

    // Returns the parent of the current state
    public State getParent() {
        return Parent;
    }
}