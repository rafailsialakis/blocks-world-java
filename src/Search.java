import java.util.*;

public class Search {

    /**
     * Selects and executes a search algorithm to solve the given problem.
     * Supported algorithms: Depth-First Search (DFS), Breadth-First Search (BFS), A* Search, and Best-First Search.
     * After finding a solution, the method prints the solution path and writes it to the output file.
     *
     * @param problem  The problem to be solved, defined by its initial and goal states.
     * @param algorithm The name of the search algorithm to use ("depth", "breadth", "astar", or "best").
     * @param output   The name of the output file to write the solution path.
     */
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
                    ArrayList<State> deepCopiedPath = new ArrayList<>(path);
                    // Print only the moves from the solution path
                    for (State state : deepCopiedPath) {
                        state.filterState().printState();  // Print the state
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Depth First Search needs " + deepCopiedPath.size() + " moves.");
                } else {
                    System.out.println("No path found!");
                }
            }
            case "breadth" -> {
                ArrayList<State> path = new ArrayList<>();

                // Perform BFS to find the path
                if (bfs(init, end, path)) {
                    System.out.println("Path found:");
                    ArrayList<State> deepCopiedPath = new ArrayList<>(path);
                    // Print the states in the solution path
                    for (State state : deepCopiedPath) {
                        state.filterState().printState();  // Print the state
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Breadth First Search needs " + path.size() + " moves.");
                } else {
                    System.out.println("No path found!");
                }
            }
            case "astar" -> {
                // Perform A* Search
                ArrayList<State> path = new ArrayList<>();
                if (aStar(init, end, path)) {
                    System.out.println("Path found:");
                    ArrayList<State> deepCopiedPath = new ArrayList<>(path);
                    for (State state : deepCopiedPath) {
                        state.filterState().printState(end, algorithm);
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("A* needs " + path.size() + " moves.");

                } else {
                    System.out.println("No path found!");
                }
            }
            case "best" -> {
                // Perform Best-First Search
                ArrayList<State> path = new ArrayList<>();
                if (bestFirst(init,end,path)) {
                    System.out.println("Path found:");
                    ArrayList<State> deepCopiedPath = new ArrayList<>(path);
                    for (State state : deepCopiedPath) {
                        state.filterState().printState(end, algorithm);
                    }
                    PDDLParser.parseOutputFile(path, output);
                    System.out.println("Best first needs " + path.size() + " moves.");
                } else {
                    System.out.println("No path found!");
                }
            }
            default -> System.out.println("Unknown algorithm: " + algorithm);
        }
    }

    /**
     * Performs Depth-First Search (DFS) to find a solution path.
     *
     * @param current The current state being explored.
     * @param goal The goal state to reach.
     * @param path The solution path if found.
     * @param visited The set of visited states to avoid loops.
     * @return True if a path is found, otherwise false.
     */
    public static boolean dfs(State current, State goal, ArrayList<State> path, Set<State> visited) {

        if (visited.contains(current)) {
            return false; // Skip if already visited
        }
        visited.add(current); // Mark current as visited
        if (current.equals(goal)) {
            path.add(current);
            return true;
        }
        for (State child : current.generateChildrenUninformed(visited)) {
            if (!visited.contains(child)) {
                if (dfs(child, goal, path, visited)) {
                    path.add(current);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Performs Breadth-First Search (BFS) to find a solution path.
     *
     * @param start The initial state.
     * @param goal The goal state to reach.
     * @param path The solution path if found.
     * @return True if a path is found, otherwise false.
     */
    public static boolean bfs(State start, State goal, ArrayList<State> path) {
        Queue<State> queue = new ArrayDeque<>(); // More efficient than LinkedList
        Set<State> visited = new HashSet<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            if (current.equals(goal)) {
                while (current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                return true;
            }
            List<State> children = current.generateChildrenUninformed(visited);
            for (State child : children) {
                child.setParent(current);
                if (!visited.contains(child)) {
                    queue.add(child);
                    child.setParent(current);
                }
            }
        }

        return false;
    }

    /**
     * Performs A* Search to find a solution path.
     *
     * @param start The initial state.
     * @param goal The goal state to reach.
     * @param path The solution path if found.
     * @return True if a path is found, otherwise false.
     */
    public static boolean aStar(State start, State goal, ArrayList<State> path) {
        PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingInt(State::getF)); // Order by f = g + h
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        // Initialize the start state
        start.setG(0); // Cost to reach start is 0
        start.setH(Problem.Heuristic(start, goal)); // Heuristic estimate
        start.setF(start.getG() + start.getH()); // f = g + h
        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll(); // Get state with lowest f value

            if (visited.contains(current)) {
                continue; // Skip already visited states
            }
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
                    child.setH(Problem.Heuristic(child, goal)); // Compute heuristic
                    child.setF(child.getG() + child.getH()); // f = g + h
                    openList.add(child); // Add to the priority queue
                    parentMap.put(child, current); // Track the parent
                }
            }
        }

        return false;
    }

    /**
     * Performs Best-First Search to find a solution path.
     *
     * @param start The initial state.
     * @param goal The goal state to reach.
     * @param path The solution path if found.
     * @return True if a path is found, otherwise false.
     */
    public static boolean bestFirst(State start, State goal, ArrayList<State> path) {
        PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingInt(State::getH));
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        // Initialize the start state
        start.setH(Problem.Heuristic(start, goal)); // Heuristic estimate
        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll(); // Get state with lowest h value

            if (visited.contains(current)) {
                continue; // Skip already visited states
            }
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
                    child.setH(Problem.Heuristic(child, goal)); // Compute heuristic
                    openList.add(child); // Add to the priority queue
                    parentMap.put(child, current); // Track the parent
                }
            }
        }

        return false; // No solution found
    }
}

