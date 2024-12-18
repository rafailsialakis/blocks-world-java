import java.util.List;

public class Problem {
    private final State s1;
    private final State s2;

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

    public static int calculateHeuristic(State currentState, State goalState) {
        int misplacedBlocks = PDDLParser.getNumberOfObjects();
        boolean found = false;
        boolean lockLoop = false;
        List<Substack> Current_Stacks = currentState.getState();
        Substack Goal_Stack = goalState.getState().getFirst();
        for(int i=0; i < PDDLParser.getNumberOfObjects(); i++) {
            Block toCompare = Goal_Stack.getBlockAtIndex(i);
            for(Substack stack: Current_Stacks){
                for(int j = 0; j < stack.size(); j++){
                    if(stack.getBlockAtIndex(j).equals(toCompare) && !lockLoop && i == j){
                        misplacedBlocks--;
                        found = true;
                    }
                }
            }
            if(!found){
                lockLoop = true;
            }
        }
        return misplacedBlocks;
    }
}
