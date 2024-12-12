import java.util.*;

public class Search {

    // Method to perform the search based on the selected algorithm
    public static void SelectSearch(Problem problem, String algorithm) {
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
                } else {
                    System.out.println("No path found!");
                }
            }
            default -> System.out.println("Unknown algorithm: " + algorithm);
        }
    }

    // Depth-First Search (DFS)
    public static boolean dfs(State current, State goal, ArrayList<State> path, Set<State> visited) {
        if (current.equals(goal)) {
            path.add(current);
            return true;
        }

        visited.add(current);

        for (State child : current.generateChildrenUninformed()) {
            if (!visited.contains(child)) {
                if (dfs(child, goal, path, visited)) {
                    path.add(current);
                    return true;
                }
            }
        }
        return false;
    }

    // Breadth-First Search (BFS)
    public static boolean bfs(State start, State goal, ArrayList<State> path) {
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.equals(goal)) {
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }

            for (State child : current.generateChildrenUninformed()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    parentMap.put(child, current);
                    queue.add(child);
                }
            }
        }

        return false;
    }

    // A* Search
    public static boolean aStar(State start, State goal, ArrayList<State> path) {
        // Priority queue to store states, ordered by f = g + h
        PriorityQueue<State> openList = new PriorityQueue<>();
        Set<State> closedList = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        start.setG(0);  // Start state has no cost to reach
        start.setH(Problem.calculateHeuristic(start, goal));  // Heuristic estimate for A*
        start.setF(start.getG() + start.getH());  // f = g + h

        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll();

            if (current.equals(goal)) {
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }

            closedList.add(current);

            for (State child : current.generateChildrenInformed(goal)) {
                if (closedList.contains(child)) {
                    continue;
                }

                // If child is not in openList, or we find a better path
                if (!openList.contains(child)) {
                    child.setG(current.getG() + 1);  // Set g value for child
                    child.setH(Problem.calculateHeuristic(child,goal));  // Set h value for child
                    child.setF(child.getG() + child.getH());  // Set f value for child
                    openList.add(child);
                    parentMap.put(child, current);
                }
            }
        }

        return false;
    }


    // Best-First Search
    public static boolean bestFirst(State start, State goal, ArrayList<State> path) {
        // Priority queue to store states, ordered by h (heuristic)
        PriorityQueue<State> openList = new PriorityQueue<>();
        Set<State> closedList = new HashSet<>();
        Map<State, State> parentMap = new HashMap<>();

        start.setH(Problem.calculateHeuristic(start, goal));  // Heuristic estimate for Best-First
        openList.add(start);

        while (!openList.isEmpty()) {
            State current = openList.poll();

            if (current.equals(goal)) {
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);
                return true;
            }

            closedList.add(current);

            for (State child : current.generateChildrenInformed(goal)) {
                if (closedList.contains(child)) {
                    continue;
                }

                // If child is not in openList, or we find a better heuristic path
                if (!openList.contains(child)) {
                    openList.add(child);
                    parentMap.put(child, current);
                }
            }
        }
        return false;
    }

}
