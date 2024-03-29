package GraphAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import GraphDesigner.*;
import MainPackage.*;
import TreeDesigner.*;

//Graph Algorithms--------------------------------------------------------

public class Algorithms{
	
	public static boolean isHamiltonian(List<Node> nodes, List<Edge> edges) {
	    // A graph is Hamiltonian if it has a cycle that visits each node of the graph exactly once
	    // We can use backtracking to check if such a cycle exists
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
	public static boolean isEulerian(List<Node> nodes, List<Edge> edges) {
	    // A graph is Eulerian if all of its vertices have even degree
	    for (Node node : nodes) {
	    	System.out.println(node.getNodeName() + " - Edges : " + node.getEdges().size());
	        if (node.getEdges().size() % 2 != 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	// Find flows in the graph
	public static List<List<Node>> findFlows(List<Node> nodes, List<Edge> edges) {
	    // Use BFS to find all possible flows
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
	                Node neighbor = edge.getStart() == current ? edge.getEnd() : edge.getStart();
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
	                Node neighbor = edge.getStart() == currentNode ? edge.getEnd() : edge.getStart();
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
	            Node neighbor = edge.getStart() == current ? edge.getEnd() : edge.getStart();
	            if (!visitedSet.contains(neighbor)) {
	                queue.add(neighbor);
	                visitedSet.add(neighbor);
	            }
	        }
	    }
	    return visited;
	}

	
}