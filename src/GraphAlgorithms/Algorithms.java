package GraphAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import GraphDesigner.*;

//Graph Algorithms--------------------------------------------------------

public class Algorithms{
	
	// A graph is Hamiltonian if it has a cycle that visits each node of the graph exactly once
    // We can use backtracking to check if such a cycle exists
	public static boolean isHamiltonian(List<Node> nodes, List<Edge> edges) {
		
	    int size = nodes.size();
	    boolean[] visited = new boolean[size];
	    Arrays.fill(visited, false);
	    
	    for (int i = 0; i < size; i++) {
	        if (isHamiltonianDFS(nodes, edges, visited, i, i, 1)) {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	private static boolean isHamiltonianDFS(List<Node> nodes, List<Edge> edges, boolean[] visited, int start, int current, int count) {
	    // If all nodes are visited exactly once and there's an edge from the last node to the starting node, return true
	    if (count == nodes.size() && hasEdge(nodes, edges, current, start)) {
	        return true;
	    }
	
	    // Mark the current node as visited
	    visited[current] = true;
	
	    // Traverse all adjacent nodes of the current node
	    for (Edge edge : nodes.get(current).getEdges()) {
	        int neighbor = edge.getStart().equals(nodes.get(current)) ? nodes.indexOf(edge.getEnd()) : nodes.indexOf(edge.getStart());
	        if (!visited[neighbor]) {
	            if (isHamiltonianDFS(nodes, edges, visited, start, neighbor, count + 1)) {
	                return true;
	            }
	        }
	    }
	
	    // Backtrack: Mark the current node as unvisited
	    visited[current] = false;
	
	    return false;
	}
	
	private static boolean hasEdge(List<Node> nodes, List<Edge> edges, int from, int to) {
	    // Check if there's an edge from 'from' to 'to' or vice versa
	    for (Edge edge : edges) {
	        if ((edge.getStart().equals(nodes.get(from)) && edge.getEnd().equals(nodes.get(to))) ||
	            (edge.getStart().equals(nodes.get(to)) && edge.getEnd().equals(nodes.get(from)))) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	// Check if the graph is Eulerian
	// A graph is Eulerian if all of its vertices have even degree
	public static boolean isEulerian(List<Node> nodes, List<Edge> edges) {
	    
	    for (Node node : nodes) {
	        if (node.getEdges().size() % 2 != 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	// Check if the graph is SemiEulerian
	// A graph is SemiEulerian if it has exactly two nodes that have odd degree
	public static boolean isSemiEulerian(List<Node> nodes) {
        int oddDegreeCount = 0;
        for (Node node : nodes) {
            if (node.getEdges().size() % 2 != 0) {
                oddDegreeCount++;
            }
        }
        return oddDegreeCount == 2;
    }
	
	// Find flows in the graph
	// Use BFS to find all possible flows
	public static List<List<Node>> findFlows(List<Node> nodes, List<Edge> edges) {
	    
	    List<List<Node>> flows = new ArrayList<>();
	    for (Node start : nodes) {
	        List<Node> flow = new ArrayList<>();
	        Queue<Node> queue = new LinkedList<>();
	        Set<Node> visited = new HashSet<>();
	        queue.add(start);
	        visited.add(start);
	        while (!queue.isEmpty()) {
	            Node current = queue.poll();
	            flow.add(current);
	            for (Edge edge : current.getEdges()) {
	                Node neighbor = edge.getEnd();
	                if (!visited.contains(neighbor)) {
	                    queue.add(neighbor);
	                    visited.add(neighbor);
	                }
	            }
	        }
	        flows.add(flow);
	    }
	    return flows;
	}

	
	public static List<Node> hierholzerEulerianPath(List<Node> nodes) {
        List<Node> circuit = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        
        if (nodes.isEmpty())
            return circuit;

        // Find a node with non-zero degree to start the traversal
        Node start = findStartNode(nodes);
        if (start == null) // No suitable start node found
            return circuit;

        stack.push(start);
        
        while (!stack.isEmpty()) {
            Node current = stack.peek();
            
            // Check if there are remaining unvisited edges from the current node
            if (!current.getEdges().isEmpty()) {
                Edge edge = current.getEdges().remove(0); // Remove the first edge
                edge.getEnd().getEdges().removeIf(e -> e.getEnd().equals(current)); // Remove the reverse edge
                stack.push(edge.getEnd()); // Move to the next node
            } else {
                circuit.add(stack.pop());
            }
        }

        return circuit;
    }

    // Find a node with non-zero degree to start the traversal
    private static Node findStartNode(List<Node> nodes) {
        for (Node node : nodes) {
            if (!node.getEdges().isEmpty())
                return node;
        }
        return null;
    }

    public static List<Node> fleuryEulerianPath(List<Node> nodes) {
        List<Node> path = new ArrayList<>();
        
        // Find a starting node with odd degree
        Node start = findStartNodeFleury(nodes);
        
        if (start == null)
            return path; // No Eulerian path exists
        
        // Perform the Eulerian tour
        eulerianTour(start, path);
        
        return path;
    }

    private static void eulerianTour(Node current, List<Node> path) {
        while (!current.getEdges().isEmpty()) {
            Edge edge = current.getEdges().get(0); // Get the first edge
            Node next = edge.getEnd();
            final Node currentNode = current; // Final copy of current node

            // If removing the edge doesn't disconnect the graph, or if it's the only option, use it
            if (isBridge(currentNode, next) || currentNode.getEdges().size() == 1) {
                currentNode.getEdges().remove(edge);
                next.getEdges().removeIf(e -> e.getEnd().equals(currentNode));
                eulerianTour(next, path);
            } else {
                // Move to the next edge
                currentNode.getEdges().remove(edge);
                next.getEdges().removeIf(e -> e.getEnd().equals(currentNode));
                path.add(currentNode);
                current = next;
            }
        }
        path.add(current);
    }


    private static boolean isBridge(Node u, Node v) {
        // Check if removing the edge disconnects the graph
        int componentsBeforeRemoval = countConnectedComponents(u.getEdges());
        
        // Temporarily remove the edge
        u.getEdges().removeIf(e -> e.getEnd().equals(v));
        v.getEdges().removeIf(e -> e.getEnd().equals(u));

        // Count the number of connected components after removal
        int componentsAfterRemoval = countConnectedComponents(u.getEdges());

        // Restore the removed edge
        u.addEdge(new Edge(u, v));
        v.addEdge(new Edge(v, u));

        return componentsAfterRemoval > componentsBeforeRemoval;
    }

    private static int countConnectedComponents(List<Edge> edges) {
        List<Node> visited = new ArrayList<>();
        int components = 0;
        for (Edge edge : edges) {
            Node start = edge.getStart();
            if (!visited.contains(start)) {
                dfs(start, visited);
                components++;
            }
        }
        return components;
    }

    private static void dfs(Node node, List<Node> visited) {
        visited.add(node);
        for (Edge edge : node.getEdges()) {
            Node neighbor = edge.getEnd();
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }

    private static Node findStartNodeFleury(List<Node> nodes) {
        for (Node node : nodes) {
            if (node.getEdges().size() % 2 != 0)
                return node;
        }
        return null;
    }

    
	// Depth-First Search (DFS)
	public static List<Node> dfs(Node start) {
	    List<Node> visited = new ArrayList<>();
	    Set<Node> visitedSet = new HashSet<>();
	    Stack<Node> stack = new Stack<>();
	    
	    stack.push(start);
	
	    while (!stack.isEmpty()) {
	        Node currentNode = stack.pop();
	        if (!visitedSet.contains(currentNode)) {
	            visited.add(currentNode);
	            visitedSet.add(currentNode);
	            for (Edge edge : currentNode.getEdges()) {
	                Node neighbor = edge.getEnd();
	                if (!visitedSet.contains(neighbor)) {
	                    stack.push(neighbor);
	                }
	            }
	        }
	    }
	
	    return visited;
	}
	
	
	// Breadth-First Search (BFS)
	public static List<Node> bfs(Node start) {
	    List<Node> visited = new ArrayList<>();
	    Queue<Node> queue = new LinkedList<>();
	    Set<Node> visitedSet = new HashSet<>();
	    queue.add(start);
	    visitedSet.add(start);
	    while (!queue.isEmpty()) {
	        Node current = queue.poll();
	        visited.add(current);
	        for (Edge edge : current.getEdges()) {
	            Node neighbor = edge.getEnd();
	            if (!visitedSet.contains(neighbor)) {
	                queue.add(neighbor);
	                visitedSet.add(neighbor);
	            }
	        }
	    }
	    return visited;
	}

	
}