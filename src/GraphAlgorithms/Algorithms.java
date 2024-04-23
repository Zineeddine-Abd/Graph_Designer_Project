package GraphAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import GraphDesigner.*;

//Graph Algorithms--------------------------------------------------------

public class Algorithms{
	
	// A graph is Hamiltonian if it has a cycle that visits each node of the graph exactly once
    // We can use backtracking to check if such a cycle exists
	
	public static boolean isHamiltonian(List<Node> nodes , List<Edge> edges) {
		
		int numberOfNodes = nodes.size();
		
		for(Node node : nodes) {
			
			if(node.getEdges().size() < numberOfNodes/2) {
				return false;
			}
		}
		
		return true;
	}
	
	
//	public static boolean isHamiltonian(List<Node> nodes, List<Edge> edges) {
//		
//	    int size = nodes.size();
//	    boolean[] visited = new boolean[size];
//	    Arrays.fill(visited, false);
//	    
//	    for (int i = 0; i < size; i++) {
//	        if (isHamiltonianDFS(nodes, edges, visited, i, i, 1)) {
//	            return true;
//	        }
//	    }
//	    
//	    return false;
//	}
//	
//	private static boolean isHamiltonianDFS(List<Node> nodes, List<Edge> edges, boolean[] visited, int start, int current, int count) {
//	    // If all nodes are visited exactly once and there's an edge from the last node to the starting node, return true
//	    if (count == nodes.size() && hasEdge(nodes, edges, current, start)) {
//	        return true;
//	    }
//	
//	    // Mark the current node as visited
//	    visited[current] = true;
//	
//	    // Traverse all adjacent nodes of the current node
//	    for (Edge edge : nodes.get(current).getEdges()) {
//	        int neighbor = edge.getStart().equals(nodes.get(current)) ? nodes.indexOf(edge.getEnd()) : nodes.indexOf(edge.getStart());
//	        if (!visited[neighbor]) {
//	            if (isHamiltonianDFS(nodes, edges, visited, start, neighbor, count + 1)) {
//	                return true;
//	            }
//	        }
//	    }
//	
//	    // Backtrack: Mark the current node as unvisited
//	    visited[current] = false;
//	
//	    return false;
//	}
//	
//	private static boolean hasEdge(List<Node> nodes, List<Edge> edges, int from, int to) {
//	    // Check if there's an edge from 'from' to 'to' or vice versa
//	    for (Edge edge : edges) {
//	        if ((edge.getStart().equals(nodes.get(from)) && edge.getEnd().equals(nodes.get(to))) ||
//	            (edge.getStart().equals(nodes.get(to)) && edge.getEnd().equals(nodes.get(from)))) {
//	            return true;
//	        }
//	    }
//	    return false;
//	}
	
	
	// Check if the graph is Eulerian
	// A graph is Eulerian if all of its vertices have even degree
	public static boolean isEulerian(List<Node> nodes) {
	    
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
	

	//hierholzer algorithm for undirected graphs
	public static List<Node> hierholzerEulerianPath(List<Node> nodes) {
        List<Node> circuit = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        
        if (nodes.isEmpty())
            return circuit;

        // Find a node with non-zero degree to start the traversal
        Node start = findStartNode(nodes);
        if (start == null) // No suitable start node found
            start = nodes.get(0);

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
            if (node.getEdges().size() % 2 != 0)
                return node;
        }
        return null;
    }
    
    public static boolean isEulerianDirected(List<Node> nodes) {
        // Calculate in-degree and out-degree for each node
        Map<Node, Integer> inDegrees = new HashMap<>();
        Map<Node, Integer> outDegrees = new HashMap<>();
        for (Node node : nodes) {
            inDegrees.put(node, 0);
            outDegrees.put(node, 0);
        }
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                outDegrees.put(node, outDegrees.get(node) + 1);
                inDegrees.put(edge.getEnd(), inDegrees.get(edge.getEnd()) + 1);
            }
        }
        
        // Check if all nodes have equal in-degree and out-degree
        for (Node node : nodes) {
            if (!inDegrees.get(node).equals(outDegrees.get(node))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isSemiEulerianDirected(List<Node> nodes) {
        // Calculate in-degree and out-degree for each node
        Map<Node, Integer> inDegrees = new HashMap<>();
        Map<Node, Integer> outDegrees = new HashMap<>();
        for (Node node : nodes) {
            inDegrees.put(node, 0);
            outDegrees.put(node, 0);
        }
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                outDegrees.put(node, outDegrees.get(node) + 1);
                inDegrees.put(edge.getEnd(), inDegrees.get(edge.getEnd()) + 1);
            }
        }
        
        // Count nodes with (out-degree - in-degree) = 1 and (in-degree - out-degree) = 1
        int startNodes = 0;
        int endNodes = 0;
        for (Node node : nodes) {
            int outDegree = outDegrees.get(node);
            int inDegree = inDegrees.get(node);
            if (outDegree - inDegree == 1) {
                startNodes++;
            } else if (inDegree - outDegree == 1) {
                endNodes++;
            } else if (outDegree != inDegree) {
                // If any other node has different in-degree and out-degree, it's not semi-Eulerian
                return false;
            }
        }
        // Semi-Eulerian graph can have either 0 or 2 nodes with (out-degree - in-degree) = 1
        return (startNodes == 0 && endNodes == 0) || (startNodes == 1 && endNodes == 1);
    }
    
    
    //hierholzer algorithm for directed graphs
    public static List<Node> hierholzerEulerianPathDirected(List<Node> nodes, List<Edge> edges) {
        List<Node> eulerianPath = new ArrayList<>();
        
        if (!hasEulerianPathOrCircuit(nodes, edges)) {
            return eulerianPath; // No Eulerian path or circuit exists
        }

        Map<Node, Integer> inDegrees = new HashMap<>();
        Map<Node, Integer> outDegrees = new HashMap<>();
        for (Node node : nodes) {
            inDegrees.put(node, 0);
            outDegrees.put(node, 0);
        }
        for (Edge edge : edges) {
            outDegrees.put(edge.getStart(), outDegrees.get(edge.getStart()) + 1);
            inDegrees.put(edge.getEnd(), inDegrees.get(edge.getEnd()) + 1);
        }

        Node startNode = null;
        for (Node node : nodes) {
            if (outDegrees.get(node) - inDegrees.get(node) == 1) {
                startNode = node; // Find the start node for the Eulerian path
                break;
            }
        }
        
        if (startNode == null) {
            // If no node has out-degree one greater than its in-degree, find any start node
            startNode = nodes.get(0);
        }

        Stack<Node> stack = new Stack<>();
        stack.push(startNode);

        // Track visited edges
        Map<Edge, Boolean> visitedEdges = new HashMap<>();
        for (Edge edge : edges) {
            visitedEdges.put(edge, false);
        }

        while (!stack.isEmpty()) {
            Node currentNode = stack.peek();
            if (outDegrees.get(currentNode) == 0) {
                eulerianPath.add(currentNode);
                stack.pop();
            } else {
                for (Edge edge : currentNode.getEdges()) {
                    if (!visitedEdges.get(edge)) {
                        visitedEdges.put(edge, true);
                        outDegrees.put(currentNode, outDegrees.get(currentNode) - 1);
                        stack.push(edge.getEnd());
                        break;
                    }
                }
            }
        }

        return eulerianPath;
    }

    public static boolean hasEulerianPathOrCircuit(List<Node> nodes, List<Edge> edges) {
        // Count nodes with odd out-degrees
        int oddOutDegreeNodes = 0;
        for (Node node : nodes) {
            if (node.getEdges().size() % 2 != 0) {
                oddOutDegreeNodes++;
            }
        }

        // Eulerian path or circuit exists if there are 0 or 2 nodes with odd out-degrees
        return oddOutDegreeNodes == 0 || oddOutDegreeNodes == 2;
    }

    
    public static List<Node> fleuryEulerianPath(List<Node> nodes) {
        List<Node> path = new ArrayList<>();
        
        // Find a starting node with odd degree
        Node start = findStartNodeFleury(nodes);
        
        if (start == null)
            start = nodes.get(0); // No Eulerian path exists
        
        // Perform the Eulerian tour
        eulerianTour(start, path , 0);
        
        return path;
    }

    private static void eulerianTour(Node current, List<Node> path , int i) {
        while (!current.getEdges().isEmpty()) {
            Edge edge = current.getEdges().get(i); // Get the first edge
            Node next = edge.getEnd();
            final Node currentNode = current; // Final copy of current node

            // If removing the edge doesn't disconnect the graph, or if it's the only option, use it
            if (!isBridge(currentNode, next) && currentNode.getEdges().size() != 1) {
                currentNode.getEdges().remove(edge);
                i++;
                Node nextNode = currentNode.getEdges().get(i).getEnd();
                eulerianTour(nextNode, path , i);
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