import java.util.*;

public class Problem {
    private final State s1;
    private final State s2;

    public Problem(State s1, State s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public State getInit() {
        return s1;
    }

    public State getFinal() {
        return s2;
    }

    /**
     * Heuristic function to estimate the cost of reaching the goal state from the current state.
     *
     * The heuristic calculates:
     * 1. The number of misplaced blocks (blocks that are not in their correct position in the goal stack).
     * 2. A penalty for each block that is misplaced based on its height from the top in the current stack.
     * 3. Additional penalties for blocks that violate the goal order, where a block above another in the current state
     *    should not appear below it in the goal stack.
     *
     * Steps:
     * - Map the positions of blocks in the goal stack for quick lookup.
     * - Compute the heights of blocks in the current state for penalty calculation.
     * - Iterate through each stack in the current state and compare the positions of blocks with their goal positions.
     * - Accumulate penalties for misplaced blocks and out-of-order dependencies.
     *
     * The heuristic returns the sum of misplaced blocks and the penalty score.
     * This approach provides an admissible and consistent heuristic for solving the block-stacking problem.
     *
     * @param currentState The current state of the blocks.
     * @param goalState The desired goal state of the blocks.
     * @return The heuristic cost as an integer.
     */
    public static int Heuristic(State currentState, State goalState) {
        int misplacedBlocks = 0;
        int penalty = 0;

        List<Substack> currentStacks = currentState.getState();
        Substack goalStack = goalState.getState().getFirst(); // Assuming a single goal stack

        // Create a map for quick lookup of goal positions and block heights in the goal stack
        Map<Block, Integer> goalPositions = new HashMap<>();
        for (int i = 0; i < goalStack.size(); i++) {
            goalPositions.put(goalStack.getBlockAtIndex(i), i);
        }

        // Precompute heights of blocks in the current state
        Map<Block, Integer> currentHeights = new HashMap<>();
        for (Substack stack : currentStacks) {
            for (int j = 0; j < stack.size(); j++) {
                currentHeights.put(stack.getBlockAtIndex(j), stack.size() - j - 1); // Height from top
            }
        }
        // Iterate over all stacks in the current state
        for (Substack currentStack : currentStacks) {
            for (int j = 0; j < currentStack.size(); j++) {
                Block currentBlock = currentStack.getBlockAtIndex(j);

                // Check if the block exists in the goal stack
                Integer goalIndex = goalPositions.get(currentBlock);

                if (goalIndex != null) {
                    // Block exists in the goal stack
                    if (!goalIndex.equals(j)) {
                        // Misplaced block
                        misplacedBlocks++;

                        // Penalize blocks above the current block
                        penalty += currentHeights.get(currentBlock);

                        // Penalize out-of-order dependencies
                        for (int k = j + 1; k < currentStack.size(); k++) {
                            Block aboveBlock = currentStack.getBlockAtIndex(k);
                            Integer aboveGoalIndex = goalPositions.get(aboveBlock);
                            if (aboveGoalIndex != null && aboveGoalIndex < goalIndex) {
                                penalty++; // Misplaced dependency penalty
                            }
                        }
                    }
                } else {
                    // Block is irrelevant or misplaced (not in the goal stack)
                    misplacedBlocks++;
                }
            }
        }
        return misplacedBlocks + penalty;
    }

}