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
