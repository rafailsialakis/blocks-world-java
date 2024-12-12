import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Problem {
    private State s1;
    private State s2;

    public Problem(State s1, State s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public State getInit()
    {
        return s1;
    }

    public State getFinal()
    {
        return s2;
    }

    public static double calculateHeuristic(State currentState, State goalState) {
        double misplacedBlocks = 0;

        // Iterate through each stack in the current state
        for (int currentStackIndex = 0; currentStackIndex < currentState.getState().size(); currentStackIndex++) {
            Substack currentStack = currentState.getState().get(currentStackIndex);

            // Iterate through each block in the current stack
            for (Block block : currentStack.getBlocks()) {

                // Find the index of the stack the block should be in according to the goal state
                int goalStackIndex = -1;
                for (int goalStackIndexInGoal = 0; goalStackIndexInGoal < goalState.getState().size(); goalStackIndexInGoal++) {
                    Substack goalStack = goalState.getState().get(goalStackIndexInGoal);

                    // Check if the goal stack contains the current block
                    if (goalStack.contains(block)) {
                        goalStackIndex = goalStackIndexInGoal;
                        break;
                    }
                }

                // If the block is in the wrong stack, increment the misplaced blocks counter
                if (goalStackIndex != currentStackIndex) {
                    misplacedBlocks++;
                }
            }
        }

        return misplacedBlocks; // Return the number of misplaced blocks
    }









}
