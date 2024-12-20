/*
    Author: Sialakis Rafail
    Main class for the Blocks World problem-solving application.

    This program takes a planning problem defined in a PDDL input file and solves it using a specified search algorithm.
    The solution is written to an output file, and key details (initial and final states, performance metrics) are displayed
    on the console.

    Usage:
    - The program can be executed with command-line arguments specifying the search algorithm, input file, and output file.
    - If no arguments are provided, a usage guide is displayed.

    Methods:
    - main(String[] args): Entry point for the application.
    - AgentActions(String[] args): Parses the input file, solves the problem using the specified algorithm, and writes the solution.
    - printInfo(): Displays usage information and program details.
*/

public class Main {
    public static void main(String[] args) {
        /*
        Entry point for the program.
        - If exactly three command-line arguments are provided, the AgentActions method is invoked to solve the problem.
        - Otherwise, the printInfo method is called to display usage instructions.
        
        Parameters:
        - args: Command-line arguments (algorithm, input file, output file).
        */
        if (args.length == 3) {
            Main.AgentActions(args);
        } else {
            Main.printInfo();
        }
    }

    static void AgentActions(String[] args) {
        /*
        Handles the main logic of parsing the input file, solving the problem using the specified algorithm, 
        and saving the solution to the output file.
        
        Parameters:
        - args: An array containing the search algorithm, input file name, and output file name.
        
        Steps:
        1. Parse the input file to create the initial and goal states.
        2. Display the initial and goal states on the console.
        3. Solve the problem using the specified algorithm, with a 60-second timeout for execution.
        4. Save the solution plan to the output file and display the elapsed time.
        */
        SIGAlarm alarm = new SIGAlarm(); // Alarm to enforce a 60-second timeout
        long time1, time2, time3, time4;

        String algo = args[0]; // Algorithm to use
        String infileName = args[1]; // Input file name
        String outfileName = args[2]; // Output file name

        // Parse the input file to create the Problem instance
        PDDLParser parser = new PDDLParser(infileName);
        time1 = System.currentTimeMillis();
        Problem problem = parser.parseInputFile();
        time2 = System.currentTimeMillis();

        // Display the initial and final states
        System.out.println("Initial State:");
        problem.getInit().filterState().printState();
        System.out.println("Final State:");
        problem.getFinal().filterState().printState();
        System.out.println("Problem parsed in: " + (time2 - time1) / 1000.0 + " seconds.\n");

        // Set an alarm for 60 seconds and solve the problem
        alarm.setAlarm(60);
        time3 = System.currentTimeMillis();
        Search.SelectSearch(problem, algo, outfileName); // Perform the search
        time4 = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (time4 - time3) / 1000.0 + " seconds.");
        alarm.cancelAlarm();
    }

    static void printInfo() {
        /*
        Displays usage instructions and program details on the console.
        
        Provides information about:
        - The required command-line arguments (algorithm, input file, output file).
        - Available search algorithms and their descriptions.
        - Example usage and notes on input file formatting and supported algorithms.
        */
        System.out.println("Usage: java -jar <program-name>.jar <algorithm> <input-file> <output-file>");
        System.out.println("\nDescription:");
        System.out.println("  This program solves planning problems based on the provided PDDL input file.");
        System.out.println("\nArguments:");
        System.out.println("  <algorithm>: The search algorithm to use. Options are:");
        System.out.println("               - 'breadth': Breadth-first search");
        System.out.println("               - 'depth': Depth-first search");
        System.out.println("               - 'astar': A* search");
        System.out.println("               - 'best': Best-first search");
        System.out.println("\n  <input-file>: Path to the input PDDL file defining the planning problem.");
        System.out.println("  <output-file>: Path to save the generated solution plan.");
        System.out.println("\nExample:");
        System.out.println("  java -jar -Xmx1024m -Xms1024m blocks-world.jar astar probBLOCKS-5-2.pddl solution.txt");
        System.out.println("\nNote:");
        System.out.println("  Ensure the input PDDL file is correctly formatted and contains the domain and problem definition.");
        System.out.println("  Supported algorithms require heuristic functions for optimal performance.");
    }
}
