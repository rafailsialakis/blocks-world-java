import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.*;

public class PDDLParser{
    private final String filename;
    private static int numberOfObjects;

    public PDDLParser(String filename) {
        this.filename = filename;
    }

    /*
    Parser is a method that takes no parameters and returns as an output a Problem object with
    2 States. The Initial State and the Final State. The format of the files to be parsed are .pddl
    referring to the blocks world problem https://www.cs.colostate.edu/meps/repository/aips2000.html#blocks
     */

    public Problem parseInputFile(){
        try {
            StringBuilder objects = new StringBuilder(); //StringBuilder to read Objects as Strings
            StringBuilder initial_statement = new StringBuilder(); //StringBuilder to read INIT statement
            StringBuilder final_statement = new StringBuilder(); //StringBuilder to read FINAL statement
            FileReader fr = new FileReader("Problems/" + filename); //Initialize Filereader & BufferedReader
            BufferedReader br = new BufferedReader(fr);
            ArrayList<Block> Blocks = new ArrayList<>(); //ArrayList of Blocks to store Blocks after they are parsed
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
                Blocks.add(new Block(name));
            numberOfObjects = Blocks.size();
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
            Initial_State.addStacksToInitialize(Blocks);
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

    public static void parseOutputFile(ArrayList<State> Path, String filename) {
        if (Path.size() < 2) {
            System.out.println("Error: Path must contain at least two states to compute moves.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Output/" + filename))) {
            for (int i = 0; i < Path.size() - 1; i++) {
                State first = Path.get(i);
                State second = Path.get(i + 1);
                String move = findMove(first, second);
                writer.write(move);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String findMove(State first, State second) {
        Block movedBlock = null;
        String bottomBlock = null;
        String onBlock = null;
        int topindex = 0;
        PDDLParser.filterMove(first,second);
        if (first.getCountSubstacks() == second.getCountSubstacks()) {
            for (int i = 0; i < first.getState().size(); i++) {
                Substack stack1 = first.getState().get(i);
                Substack stack2 = second.getState().get(i);
                if (stack1.size() > stack2.size()) {
                    movedBlock = stack1.peek();
                    if(stack1.size() == 1){
                        bottomBlock = "table";
                    }
                    else{
                        topindex = stack1.getIndexByElement(stack1.peek()); //index of top element
                        bottomBlock = stack1.getBlockAtIndex(topindex - 1).toString();
                    }
                }
            }
            for (int i = 0; i < first.getState().size(); i++) {
                Substack stack1 = first.getState().get(i);
                Substack stack2 = second.getState().get(i);

                if (stack1.size() < stack2.size()) {
                    if (stack1.isEmpty()) {
                        onBlock = "table";
                    } else {
                        onBlock = stack1.peek().toString();
                    }
                }
            }
        }
        else{
            List<Substack> state = first.getState();
            for(Substack stack: state){
                if(stack.size() == 1){
                    movedBlock = stack.peek();
                    bottomBlock = "table";
                }
                else if(!stack.isEmpty()){
                    onBlock = stack.peek().toString();
                    topindex = stack.getIndexByElement(stack.peek()); //index of top element
                    bottomBlock = stack.getBlockAtIndex(topindex - 1).toString();
                }
            }
        }
        return "MOVE(" + movedBlock + ", " + bottomBlock + ", " + onBlock + ")\n";
    }

    public static void filterMove(State s1, State s2) {
        List<Substack> list1 = s1.getState();
        List<Substack> list2 = s2.getState();
        int sizeofList1 = list1.size();
        int sizeofList2 = list2.size();
        int count = 0;

        // If list1 has more substacks than list2
        if (sizeofList1 > sizeofList2) {
            int countToRemove = sizeofList1 - sizeofList2;
            List<Substack> toRemove = new ArrayList<>(); // Temporary list to collect empty stacks to remove
            for (Substack stack : list1) {
                if (stack.isEmpty() && count < countToRemove) {
                    toRemove.add(stack); // Add empty stack to list
                    count++;
                }
            }
            // Remove only the empty stacks from list1 to match the size of list2
            for(Substack stack: toRemove){
                s1.removeSubstack(stack);
            }
        }
        // If list2 has more substacks than list1
        else if (sizeofList1 < sizeofList2) {
            int countToRemove = sizeofList2 - sizeofList1;
            List<Substack> toRemove = new ArrayList<>(); // Temporary list to collect empty stacks to remove
            for (Substack stack : list2) {
                if (stack.isEmpty() && count < countToRemove) {
                    toRemove.add(stack); // Add empty stack to list
                    count++;
                }
            }
            // Remove only the empty stacks from list2 to match the size of list1
            for(Substack stack: toRemove){
                s2.removeSubstack(stack);
            }
        }
    }

    /*
    This method takes as parameters a Set<Block> and an integer that refers to the
    index of the set. When working with HashSets we cannot use get(index), so we are
    forced to use iterators or enhanced for loops.
    Returns a Block object
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

    public static Block getBlockByName(ArrayList<Block> blocks, String name) {
        for (Block block : blocks) {
            if (block.name().equals(name)) {
                return block;
            }
        }
        return null; // Return null if no block found with the given name
    }

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

    public static int getNumberOfObjects(){
        return numberOfObjects;
    }
}