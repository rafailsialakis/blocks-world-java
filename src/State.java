import java.util.*;

public class State implements Comparable<State> {
    private State Parent;
    private final ArrayList<Substack> state;
    private static int countSubstacks;
    private int g;  // Cost to reach this state (e.g., number of moves)
    private int h;  // Heuristic value (estimate to reach the goal)
    private int f;  // Total cost (f = g + h for A*)

    public State(Substack... substacks) {
        state = new ArrayList<>();
        Collections.addAll(state, substacks);
        countSubstacks = 0;
        Parent = null;
    }

    public void setParent(State parent){
        Parent = parent;
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
        int misplacedBlocks = Problem.calculateHeuristic(this, goalState);
        System.out.println("Misplaced blocks: " + misplacedBlocks);
        System.out.println();
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public int getF() {
        return f;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setF(int f) {
        this.f = f;
    }

    public List<Substack> getState() {
        return state;
    }


    // Generate children for uninformed search (no heuristic)
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

                // Apply the filter to reduce empty stacks to a single one
                filterChild(childState);
                // Add the child state if it's not already visited
                if (!visited.contains(childState) && !children.contains(childState)) {
                    childState.setParent(this);
                    children.add(childState);
                    //System.out.println(childState);
                    //childState.printState();
                }
            }
        }
        return children;
    }
/*
    public boolean stupidMove(State s1, State s2){
        List<Substack> state1 = s1.getState();
        List<Substack> state2 = s2.getState();
    }
*/
    public int getCountSubstacks(){
        return state.size();
    }

    public void removeSubstack(Substack stack) {
        // Remove the specific substack from the state list
        state.remove(stack);
    }


    // Helper method to check if the state has all blocks in a single stack
    public boolean isSingleStackState() {
        int nonEmptyStacks = 0;
        for (Substack stack : state) {
            if (!stack.isEmpty()) {
                nonEmptyStacks++;
            }
        }
        return nonEmptyStacks == 1; // Only one non-empty stack remains
    }

    public static State filterState(State s) {
        List<Substack> state = s.getState();

        /*
        while (iterator.hasNext()) {
            Substack stack = iterator.next();
            if (stack.isEmpty()) {
                iterator.remove(); // Safe removal using Iterator
            }
        }
         */

        // Safe removal using Iterator
        state.removeIf(Substack::isEmpty);
        return s;
    }

    public static void filterChild(State s){
        List<Substack> state = s.getState();
        state.removeIf(Substack::isEmpty);
        state.add(new Substack());
    }

    // Helper method to check if a move is redundant (no real change)
    private boolean isRedundantMove(List<Substack> newStacks, int fromIndex, int toIndex) {
        Substack originalSource = state.get(fromIndex);
        Substack originalDest = state.get(toIndex);
        Substack newSource = newStacks.get(fromIndex);
        Substack newDest = newStacks.get(toIndex);

        // Check if the move simply "undoes" a previous state (swap back)
        return newSource.equals(originalDest) && newDest.equals(originalSource);
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

                // Apply the filter to reduce empty stacks to a single one
                filterChild(childState);

                // Add the child state if it's not already visited or generated
                if (!visited.contains(childState) && !children.contains(childState)) {
                    childState.setParent(this);
                    // Calculate the heuristic value for the child state (h)
                    int heuristic = Problem.calculateHeuristic(childState, goal);
                    childState.setH(heuristic);

                    // Set cost values for A* (g = parent g + 1)
                    childState.setG(this.g + 1); // Increment the cost from parent
                    childState.setF(childState.getG() + childState.getH()); // f = g + h

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
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        State otherState = (State) obj;

        // Compare normalized states
        List<Substack> thisNormalized = normalizeState(this.state);
        List<Substack> otherNormalized = normalizeState(otherState.state);

        return thisNormalized.equals(otherNormalized);
    }

    @Override
    public int hashCode() {
        // Compute hash code based on normalized state
        return normalizeState(this.state).hashCode();
    }

    // Helper to normalize a state
    private List<Substack> normalizeState(List<Substack> stacks) {
        List<Substack> normalized = new ArrayList<>(stacks);
        normalized.removeIf(Substack::isEmpty); // Remove empty substacks
        normalized.sort(Comparator.comparing(Object::toString)); // Sort for consistency
        return normalized;
    }


    public State getParent() {
        return Parent;
    }
}
