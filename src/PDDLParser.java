import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.*;
import java.util.Comparator;

public class PDDLParser{
    private final String filename;
    private static ArrayList<Block> Blocks;

    public PDDLParser(String filename) {
        this.filename = filename;
    }

    /*
    Parser is a method that takes no parameters and returns as an output a Problem object with
    2 States. The Initial State and the Final State. The format of the files to be parsed are .pddl
    referring to the blocks world problem https://www.cs.colostate.edu/meps/repository/aips2000.html#blocks

    First of all the parser discards the first 2 lines of the file, which are irrelevant to the solution.
    After, the parser reads the objects until he finds ")", and appends them to String. The String gets
    filtered, so to have only the names and spaces. Because of some harder problems for example
    probBLOCKS-50-0.pddl, the objects are in two lines and spaces appear between them, so we have to check
    if the object is null when we append them into a list.

    In the initial state, we parse until (HANDEMPTY) and sort the Blocks on CLEAR, ONTABLE and ON.
    For the ON blocks, I used a UniqueSet in order to remove duplicates. In order to create substacks
    as they should be, I read ON after it was created, until i found an object which is on table. Then
    i reversed the set and push them into a substack. I did this until the HashSet on was read. But also
    there is the possibility of some Blocks to not be on the state yet. The ones that are CLEAR and ONTABLE.
    So i took the intersection of the Lists and for each one of these Blocks, i created a new substack to
    append them to the state.

    For the final state it's pretty straight forward. We just need to read the rest of the file, filter the
    output and reverse each object and add to a new state.
    */

    public Problem parseInputFile(){
        try {
            StringBuilder objects = new StringBuilder(); //StringBuilder to read Objects as Strings
            StringBuilder initial_statement = new StringBuilder(); //StringBuilder to read INIT statement
            StringBuilder final_statement = new StringBuilder(); //StringBuilder to read FINAL statement
            FileReader fr = new FileReader("Problems/" + filename); //Initialize Filereader & BufferedReader
            BufferedReader br = new BufferedReader(fr);
            Blocks = new ArrayList<>(); //ArrayList of Blocks to store Blocks after they are parsed
            List<Block> clearList = new ArrayList<>();
            List<Block> onTableList = new ArrayList<>();
            Set<Block> uniqueSetInit = new LinkedHashSet<>();
            Set<Block> uniqueSetFinal = new LinkedHashSet<>();

            for(int i = 0; i < 2; i++)
                br.readLine();
            while(true){
                String line = br.readLine();

                objects.append(line);
                if(line.contains(")"))
                    break;
            }
            objects.replace(0, 10, "").replace(objects.length()-1, objects.length(), "");
            String objectData = objects.toString().trim();
            String[] objectNames = objectData.split(" ");

            for(String name:objectNames)
                if(!name.isEmpty())
                    Blocks.add(new Block(name));
            while(true){
                String line = br.readLine();
                initial_statement.append(line.trim()).append(" ");
                if(line.contains("(HANDEMPTY))"))
                    break;
            }
            String initData = initial_statement.toString();
            String pattern = "(CLEAR|ONTABLE|ON)\\s+([\\w]+)(?:\\s+([\\w]+))?";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(initData);
            while (m.find()) {
                String command = m.group(1);
                String firstObject = m.group(2);
                String secondObject = m.group(3);

                switch (command) {
                    case "CLEAR" -> {
                        Block block = getBlockByName(Blocks, firstObject);
                        clearList.add(block);
                    }
                    case "ONTABLE" -> {
                        Block block = getBlockByName(Blocks, firstObject);
                        onTableList.add(block);
                    }
                    case "ON" -> {
                        // For ON, add both objects separately
                        Block block1 = getBlockByName(Blocks, firstObject);
                        Block block2 = getBlockByName(Blocks, secondObject);
                        uniqueSetInit.add(block1);
                        uniqueSetInit.add(block2);
                    }
                }
            }
            State Initial_State = new State();
            int prev = 0;
            for(Block block: onTableList){
                int index = getIndexOf(uniqueSetInit, block);
                if(index != -1){
                    Substack stack = new Substack();
                    for(int i = index; i >= prev; i--){
                        stack.push(getElementAtIndex(uniqueSetInit, i));
                    }
                    prev = index + 1;
                    Initial_State.addStack(stack);
                }
            }
            List<Block> intersection = new ArrayList<>(clearList);
            intersection.retainAll(onTableList);
            for(Block block: intersection){
                Substack stack = new Substack();
                stack.push(block);
                Initial_State.addStack(stack);
            }

            while(true){
                String line = br.readLine();
                if(line != null){
                    final_statement.append(line.trim()).append(" ");
                }else {
                    break;
                }
            }
            String finalData = final_statement.toString();
            pattern = "(CLEAR|ONTABLE|ON)\\s+([\\w]+)(?:\\s+([\\w]+))?";
            r = Pattern.compile(pattern);
            m = r.matcher(finalData);
            while (m.find()) {
                String command = m.group(1);
                String firstObject = m.group(2);
                String secondObject = m.group(3);
                 if (command.equals("ON")) {
                    // For ON, add both objects separately
                     Block block1 = getBlockByName(Blocks, firstObject);
                     Block block2 = getBlockByName(Blocks, secondObject);
                     uniqueSetFinal.add(block1);
                     uniqueSetFinal.add(block2);
                }
            }
            State Final_State = new State();
            Substack stack = new Substack();
            for(int i = uniqueSetFinal.size() - 1; i >= 0; i--){
                Block block = getElementAtIndex(uniqueSetFinal, i);
                stack.push(block);
            }
            Final_State.addStack(stack);
            //Final_State.addStacksToInitialize(Blocks);
            br.close();
            return new Problem(Initial_State, Final_State);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Given a list of states representing a path, this method writes the moves
    between each state in the path to the specified output file.
    */
    public static void parseOutputFile(ArrayList<State> Path, String filename) {
        if (Path.size() < 2) {
            System.out.println("Error: Path must contain at least two states to compute moves.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Output/" + filename))) {
            // Iterate through each pair of consecutive states
            for (int i = 0; i < Path.size() - 1; i++) {
                State first = Path.get(i);
                State second = Path.get(i + 1);

                // Get the move action from the first state that led to the second state
                //String move = secondb.findMove();
                // Write the move to the output file
                String move = PDDLParser.findMove(first,second);
                writer.write(move);
                writer.newLine(); // Add a newline after each move
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Finds the move required to transform one state into the next state in a path.
    After filtering equal substacks, in a state we have 3 possible outcomes.

    1) The first state has 2 substacks and the second state has 2 substacks:

    [A,B,C]
    [D]

    To the state:

    [A,B]
    [C,D]

    2) The first state has 1 substack and the second state has 2 substacks

    [A,B,C,D]

    To the state:

    [A,B,C]
    [D]

    3) The first state has 2 substacks and the second state has 1 substack.

    [A]
    [B,C,D]

    To the state:

    [B,C,D,A]

    Returns a string representing the move (e.g., "MOVE(A, B, C)").
    */

    public static String findMove(State first, State second){
        List<Substack> firstState = first.getState();
        List<Substack> secondState = second.getState();
        String MoveBlock = "";
        String FromBlock = "";
        String ToBlock = "";


        // Create deep copies of the original state lists to work with
        List<Substack> tempState1 = new ArrayList<>(firstState);
        List<Substack> tempState2 = new ArrayList<>(secondState);

        // Now pass the copies to filterEqualSubstacks
        PDDLParser.filterEqualSubstacks(tempState1, tempState2);
        if(firstState.size() == secondState.size()) {
            for (int i = 0; i < Math.min(tempState1.size(), tempState2.size()); i++) {
                Substack initStack = tempState1.get(i);
                Substack goalStack = tempState2.get(i);

                // Compare the sizes and determine the move
                if (initStack.size() > goalStack.size()) {
                    MoveBlock = initStack.peek().toString();
                    if (initStack.size() == 1) {
                        FromBlock = "table";
                    } else {
                        FromBlock = initStack.getBlockAtIndex(initStack.size() - 2).toString();
                    }
                } else {
                    if (goalStack.size() == 1) {
                        ToBlock = "table";
                    } else {
                        ToBlock = goalStack.getBlockAtIndex(goalStack.size()-2).toString();
                    }
                }
            }
        }else{
            if(tempState1.size() < tempState2.size()){
                Substack stackOfInterest = new Substack(new State(tempState1).findLargestSubstack());
                MoveBlock = stackOfInterest.peek().toString();
                if(stackOfInterest.size() == 1){
                    FromBlock = "table";
                }else{
                    FromBlock = stackOfInterest.getBlockAtIndex(stackOfInterest.size()-2).toString();
                }
                stackOfInterest.pop();
                for(Substack st: tempState2){
                    if(!st.equals(stackOfInterest)){
                        if(st.size()==1){
                            ToBlock = "table";
                        }else{
                            ToBlock = st.getBlockAtIndex(st.size()-2).toString();
                        }
                    }
                }
            }else{
                Substack stackOfInterest = new Substack(new State(tempState2).findLargestSubstack());
                Substack secondStackOfInterest = new Substack();
                Block Move = stackOfInterest.peek();
                MoveBlock = Move.toString();
                if (stackOfInterest.size() == 1){
                    ToBlock = "table";
                }else{
                    ToBlock = stackOfInterest.getBlockAtIndex(stackOfInterest.size()-2).toString();
                }
                for(Substack s: tempState1){
                    if(s.peek().equals(Move)){
                        secondStackOfInterest = s;
                        break;
                    }
                }
                if(secondStackOfInterest.size() == 1){
                    FromBlock = "table";
                }
            }
        }
        return "MOVE(" + MoveBlock + ", " + FromBlock + ", " + ToBlock + ")";
    }

    /*
   Removes equal substacks from the two lists of substacks. This is used to filter out
   redundant stacks when comparing the initial and final states. Having first state and second state,
   if a block is moved, then only two substacks are changed. All the other ones stay the same.
   So the logic is to remove all the equal substacks of the states and to compare only the ones that
   differ.
   */
    public static void filterEqualSubstacks(List<Substack> firstStateSubstacks, List<Substack> secondStateSubstacks) {
        // Iterate over all substacks in the first state
        for (int i = 0; i < firstStateSubstacks.size(); i++) {
            Substack firstStack = firstStateSubstacks.get(i);

            // Compare it with all substacks in the second state
            for (int j = 0; j < secondStateSubstacks.size(); j++) {
                Substack secondStack = secondStateSubstacks.get(j);

                // Check if the current substacks are equal
                if (firstStack.equals(secondStack)) {
                    // Remove the matching substack from both lists
                    secondStateSubstacks.remove(j);
                    firstStateSubstacks.remove(i);

                    // Adjust the index to account for the shift caused by removal
                    i--; // Decrement i to recheck the current position in firstStateSubstacks
                    // Break out of the inner loop as we found and removed a matching pair
                    break;
                }
            }
        }
    }

    /*
    Searches the list of blocks to find a block by its name.
    Returns the Block object with the specified name, or null if not found.
    */
    public static Block getBlockByName(ArrayList<Block> blocks, String name) {
        for (Block block : blocks) {
            if (block.name().equals(name)) {
                return block;
            }
        }
        return null; // Return null if no block found with the given name
    }

    /*
    Returns the index of the specified block in the given set of blocks.
    If the block is not found, it returns -1.
    */
    public static int getIndexOf(Set<Block> set, Block element) {
        int index = 0;
        // Iterate over the LinkedHashSet
        for (Block block : set) {
            if (block.equals(element)) {
                return index; // Return the index when element is found
            }
            index++;
        }
        return -1; // Return -1 if element is not found
    }

    /*
    Retrieves the block at the specified index from the set.
    Returns the block, or null if the index is out of bounds.
    */
    public static Block getElementAtIndex(Set<Block> set, int index) {
        int currentIndex = 0;
        for (Block element : set) {
            if (currentIndex == index) {
                return element;
            }
            currentIndex++;
        }
        return null; // If not found
    }

    /*
    Returns an ArrayList<Block >of all blocks parsed in the
    parser.
     */
    public static ArrayList<Block> getBlocks(){
        return Blocks;
    }
}