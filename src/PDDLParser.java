import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.*;

public class PDDLParser{
    private final String filename;

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
            StringBuilder objects = new StringBuilder();
            StringBuilder initial_statement = new StringBuilder();
            StringBuilder final_statement = new StringBuilder();
            FileReader fr = new FileReader("Problems/" + filename);
            BufferedReader br = new BufferedReader(fr);
            ArrayList<Block> Blocks = new ArrayList<>();
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
            while(true){
                String line = br.readLine();
                initial_statement.append(line.trim()).append(" ");
                if(line.contains("(HANDEMPTY))"))
                    break;
            }
            String initData = initial_statement.toString();
            String pattern = "(CLEAR|ONTABLE|ON)\\s+([\\w\\d]+)(?:\\s+([\\w\\d]+))?";
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
            pattern = "(CLEAR|ONTABLE|ON)\\s+([\\w\\d]+)(?:\\s+([\\w\\d]+))?";
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
            Final_State.addStacksToInitialize(Blocks);
            br.close();
            return new Problem(Initial_State, Final_State);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseOutputFile(ArrayList<State> Path, String filename)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Output/" + filename));
            for(int i = 0; i < Path.size()-1; i++){
                State first = Path.get(i);
                State second = Path.get(i+1);
                String move = findMove(first,second);
                writer.write(move);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String findMove(State first, State second){
        String movedBlock = null;
        String onBlock = null;
        for(int i = 0; i < first.getState().size(); i++){
            Substack stack1 = first.getState().get(i);
            Substack stack2 = second.getState().get(i);
            if (stack1.size() > stack2.size()) {
                movedBlock = stack1.peek().toString(); // Top block removed
            }
        }
        for (int i = 0; i < first.getState().size(); i++) {
            Substack stack1 = first.getState().get(i);
            Substack stack2 = second.getState().get(i);

            if (stack1.size() < stack2.size()) {
                if(stack1.isEmpty()){
                    onBlock = "table";
                }else {
                    onBlock = stack1.peek().toString();
                }
            }
        }
        return "MOVE " + movedBlock + " ON " + onBlock + "\n";
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
            if (block.getName().equals(name)) {
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
}