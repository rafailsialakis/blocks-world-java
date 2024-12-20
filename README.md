# Blocks World Problem Solver

This project provides a solution to the Blocks World problem using informed and uninformed search algorithms. The program reads a problem description in PDDL (Planning Domain Definition Language), parses the data, and solves the problem using the specified search algorithm.

## Table of Contents
1. [Program Execution](#program-execution)
2. [Search Algorithms](#search-algorithms)
    - [Breadth-First Search (BFS)](#breadth-first-search-bfs)
    - [Depth-First Search (DFS)](#depth-first-search-dfs)
    - [Best-First Search](#best-first-search)
    - [A* Search](#a-search)
3. [Snapshots](#snapshots)

---

## Program Execution

To execute the program, use the following command:

```bash
java -jar blocksworld.jar <algorithm> <input-file> <output-file>
```

## Execution Steps

1. **Parsing**:
   - Reads the input `.pddl` file.
   - Displays:
     - The initial state.
     - The goal state.
     - The parsing time.

2. **SIGAlarm Thread**:
   - Starts a `SIGAlarm` thread to terminate execution if it exceeds 60 seconds.

3. **Search Execution**:
   - Executes the specified search algorithm as per the command-line arguments.

4. **Output**:
   - Displays:
     - The time taken to find the solution.
     - The sequence of moves (stored in the output file).

5. **Completion**:
   - Terminates the `SIGAlarm` thread.

---

## Search Algorithms

### Breadth-First Search (BFS)

#### Characteristics:
- Explores all nodes level by level.
- Always finds the **optimal solution**.
- High memory consumption, making it impractical for large problems.

#### Performance:
- Efficient for small problems.
- Struggles with large and complex cases due to memory constraints.

#### Example Execution:
- **Problem**: `prodBLOCKS.4.0.pddl`

---

### Depth-First Search (DFS)

#### Characteristics:
- Uses recursion to explore as deep as possible before backtracking.
- Does not guarantee optimal solutions.
- Requires less memory compared to BFS.
- Risk of stack overflow for large problems.

#### Optimization:
- Use the JVM flag `-Xss128m` to increase the stack size for larger problems.

#### Performance:
- Fast but often produces suboptimal solutions, especially for complex problems.

#### Example Execution:
- **Problem**: `prodBLOCKS.7.1.pddl`

---

### Best-First Search

#### Characteristics:
- A **greedy algorithm** that expands nodes with the lowest heuristic value.
- Does not guarantee optimal solutions.

#### Performance:
- Solved all tested problems up to `probBlocks-60-1.pddl`.
- Fastest among all algorithms.

#### Example Execution:
- **Problem**: `prodBLOCKS-60-1.pddl`

---

### A* Search

#### Characteristics:
- Combines path cost and heuristic value to choose the next node.
- Balances between BFS and Best-First Search.
- Slower than Best-First Search but faster than BFS.

#### Performance:
- Finds **near-optimal solutions** but not always the best due to the chosen heuristic function.

#### Example Execution:
- **Problem**: `prodBLOCKS-45-0.pddl`
