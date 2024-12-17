import java.util.*;

public class Search {
    private static int countOfIterations = 0;
    // Method to perform the search based on the selected algorithm
    public static void SelectSearch(Problem problem, String algorithm, String output) {
        State init = problem.getInit();
        State end = problem.getFinal();
        switch (algorithm) {
            case "depth" -> {
                ArrayList<State> path = new ArrayList<>();
                Set<State> visited = new HashSet<>();

                // Perform DFS to find the path
                if (dfs(init, end, path, visited)) {
                    Collections.reverse(path);
                    System.out.println("Path found:");
                    // Print only the moves from the solution path
                    for (State state : path) {
                        state.printState();  // Print the state
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Depth First Search needs " + countOfIterations + " moves.");
                } else {
                    System.out.println("No path found!");
                }
            }
            case "breadth" -> {
                ArrayList<State> path = new ArrayList<>();

                // Perform BFS to find the path
                if (bfs(init, end, path)) {
                    System.out.println("Path found:");

                    // Print the states in the solution path
                    for (State state : path) {
                        state.printState();  // Print the state
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Breadth First Search needs " + countOfIterations + " iterations.");
                } else {
                    System.out.println("No path found!");
                }
            }
            case "astar" -> {
                // Perform A* Search
                ArrayList<State> path = new ArrayList<>();
                if (aStar(init, end, path)) {
                    System.out.println("Path found:");

                    for (State state : path) {
                        state.printState(end);
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("A* needs " + countOfIterations + " moves.");

                } else {
                    System.out.println("No path found!");
                }
            }
            case "best" -> {
                // Perform Best-First Search
                ArrayList<State> path = new ArrayList<>();
                if (bestFirst(init, end, path)) {
                    System.out.println("Path found:");

                    for (State state : path) {
                        state.printState(end);
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Best first needs " + countOfIterations + " moves.");
                } else {
                    System.out.println("No path found!");
                }
            }

            default -> System.out.println("Unknown algorithm: " + algorithm);
        }
    }

    // Depth-First Search (DFS)
    public static boolean dfs(State current, State goal, ArrayList<State> path, Set<State> visited) {
        if (visited.contains(current)) {
            countOfIterations++;
            return false; // Skip if already visited
        }
        visited.add(current); // Mark current as visited
        if (current.equals(goal)) {
            path.add(current);
            return true;
        }
        for (State child : current.generateChildrenUninformed(visited)) {
            if (dfs(child, goal, path, visited)) {
                path.add(current);
                return true;
            }
        }
        return false;
    }


    // Breadth-First Search (BFS)
    public static boolean bfs(State start, State goal, ArrayList<State> path) {
        Queue<State> queue = new ArrayDeque<>(); // More efficient than LinkedList
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        queue.add(start);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }
            countOfIterations++;
            visited.add(current);

            if (current.equals(goal)) {
                // Trace back the path using the parent map
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }
            //System.out.println(visited);
            List<State> children = current.generateChildrenUninformed(visited);
            System.out.println("Children of state: ");
            current.printState();
            System.out.println("Are listed down:");
            for(State state: children)
                state.printState();
            for (State child : current.generateChildrenUninformed(visited)) {
                boolean condition = !visited.contains(child);
                //System.out.println(condition);
                if (condition) {
                    queue.add(child);
                    parentMap.put(child, current);
                }
            }
        }

        return false;
    }


    // A* Search
    public static boolean aStar(State start, State goal, ArrayList<State> path) {
        PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingDouble(State::getF)); // Order by f = g + h
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        // Initialize the start state
        start.setG(0); // Cost to reach start is 0
        start.setH(Problem.calculateHeuristic(start, goal)); // Heuristic estimate
        start.setF(start.getG() + start.getH()); // f = g + h
        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll(); // Get state with lowest f value

            if (visited.contains(current)) {
                continue; // Skip already visited states
            }
            countOfIterations++;
            visited.add(current); // Mark as visited

            if (current.equals(goal)) {
                // Build the path by tracing parentMap
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }

            for (State child : current.generateChildrenInformed(goal, visited)) {
                if (!visited.contains(child)) {
                    child.setG(current.getG() + 1); // Increment path cost
                    child.setH(Problem.calculateHeuristic(child, goal)); // Compute heuristic
                    child.setF(child.getG() + child.getH()); // f = g + h
                    openList.add(child); // Add to the priority queue
                    parentMap.put(child, current); // Track the parent
                }
            }
        }

        return false; // No solution found
    }


    // Best-First Search
    public static boolean bestFirst(State start, State goal, ArrayList<State> path) {
        PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingDouble(State::getH));
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        // Initialize the start state
        start.setH(Problem.calculateHeuristic(start, goal)); // Heuristic estimate
        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll(); // Get state with lowest h value

            if (visited.contains(current)) {
                continue; // Skip already visited states
            }
            countOfIterations++;
            visited.add(current); // Mark as visited

            if (current.equals(goal)) {
                // Build the path by tracing parentMap
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }

            for (State child : current.generateChildrenInformed(goal, visited)) {
                if (!visited.contains(child)) {
                    child.setH(Problem.calculateHeuristic(child, goal)); // Compute heuristic
                    openList.add(child); // Add to the priority queue
                    parentMap.put(child, current); // Track the parent
                }
            }
        }

        return false; // No solution found
    }
}

