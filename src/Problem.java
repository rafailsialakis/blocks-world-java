import java.util.*;

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
            Substack goalStack = goalState.getState().get(currentStackIndex); // Goal stack at the same index

            // Compare block positions in the current stack and the goal stack
            for (int blockIndex = 0; blockIndex < currentStack.getBlocks().size(); blockIndex++) {
                Block currentBlock = currentStack.getBlocks().get(blockIndex);

                // Check if the block exists at the same index in the goal stack
                if (blockIndex >= goalStack.getBlocks().size() || !currentBlock.equals(goalStack.getBlocks().get(blockIndex))) {
                    misplacedBlocks++;
                }
            }
        }

        return misplacedBlocks;
    }
}
