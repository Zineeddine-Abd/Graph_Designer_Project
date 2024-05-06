package GraphAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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
	
	
	// Hamiltonian Algorithm to check if a directed graph is Hamiltonian
    public static boolean isHamiltonianDirected(List<Node> nodes, List<Edge> edges) {
        for (Node node : nodes) {
            if (isHamiltonianDFS(node, new ArrayList<>(nodes), edges, new ArrayList<>())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHamiltonianDFS(Node currentNode, List<Node> remainingNodes, List<Edge> edges, List<Node> path) {
        path.add(currentNode);
        remainingNodes.remove(currentNode);

        if (remainingNodes.isEmpty()) {
            // All nodes visited once, check if last node has an edge to the starting node
            Node startNode = path.get(0);
            for (Edge edge : currentNode.getEdges()) {
                if (edge.getEnd() == startNode) {
                    return true; // Hamiltonian cycle found
                }
            }
            return false; // Hamiltonian path found, but not a cycle
        }

        for (Edge edge : currentNode.getEdges()) {
            Node nextNode = edge.getEnd();
            if (remainingNodes.contains(nextNode)) {
                if (isHamiltonianDFS(nextNode, new ArrayList<>(remainingNodes), edges, new ArrayList<>(path))) {
                    return true;
                }
            }
        }

        return false;
    }
	
	
	// Check Eulerian for undirected graphs
	// A graph is Eulerian if all of its vertices have even degree
	public static boolean isEulerian(List<Node> nodes) {
	    
	    for (Node node : nodes) {
	        if (node.getEdges().size() % 2 != 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	// Check Semi Eulerian for undirected graphs
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
	public static List<Node> hierholzer(List<Node> nodes) {
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
    
 // Algorithm to check if a directed graph is Eulerian
    public static boolean isEulerianDirected(List<Node> nodes, List<Edge> edges) {
        // Eulerian graph theorem for directed graph:
        // Every vertex must have equal in-degree and out-degree
        
        // Calculate in-degree and out-degree for each vertex
        int[] inDegree = new int[nodes.size()];
        int[] outDegree = new int[nodes.size()];
        
        for (Edge edge : edges) {
            int endIndex = nodes.indexOf(edge.getEnd());
            outDegree[nodes.indexOf(edge.getStart())]++;
            inDegree[endIndex]++;
        }
        
        // Check if all vertices have equal in-degree and out-degree
        for (int i = 0; i < nodes.size(); i++) {
            if (inDegree[i] != outDegree[i]) {
                return false;
            }
        }
        
        return true;
    }
    
    // Algorithm to check if a directed graph is semi-Eulerian
    public static boolean isSemiEulerianDirected(List<Node> nodes, List<Edge> edges) {
        // Semi-Eulerian graph theorem for directed graph:
        // 1. Either there is exactly one vertex with (out-degree - in-degree) = 1 and one vertex with (in-degree - out-degree) = 1
        //    and all other vertices have equal in-degree and out-degree
        // 2. Or there is exactly one vertex with (out-degree - in-degree) = 1 and one vertex with (in-degree - out-degree) = 1
        //    and all other vertices have equal in-degree and out-degree
        
        // Calculate in-degree and out-degree for each vertex
        int[] inDegree = new int[nodes.size()];
        int[] outDegree = new int[nodes.size()];
        
        for (Edge edge : edges) {
            int endIndex = nodes.indexOf(edge.getEnd());
            outDegree[nodes.indexOf(edge.getStart())]++;
            inDegree[endIndex]++;
        }
        
        // Count vertices with (out-degree - in-degree) = 1 and (in-degree - out-degree) = 1
        int outMinusInCount = 0;
        int inMinusOutCount = 0;
        
        for (int i = 0; i < nodes.size(); i++) {
            int diff = outDegree[i] - inDegree[i];
            if (diff == 1) {
                outMinusInCount++;
            } else if (diff == -1) {
                inMinusOutCount++;
            } else if (diff != 0) {
                // If any vertex has difference other than 0, return false
                return false;
            }
        }
        
        // Check if conditions for semi-Eulerian are satisfied
        return (outMinusInCount == 1 && inMinusOutCount == 1);
    }
    
    
 // Hierholzer's Algorithm to find Eulerian path in a directed graph
    public static List<Node> hierholzerDirected(List<Node> nodes, List<Edge> edges) {
        List<Node> path = new ArrayList<>();
        
        // Check if the graph is Eulerian or semi-Eulerian
        if (!isEulerianDirected(nodes, edges) && !isSemiEulerianDirected(nodes, edges)) {
            // If the graph is not Eulerian or semi-Eulerian, return an empty path
            return path;
        }
        
        // Initialize a copy of the edges list to track visited edges
        List<Edge> remainingEdges = new ArrayList<>(edges);
        
        // Initialize a map to track visited edges for each node
        Map<Node, List<Edge>> visitedEdgesMap = new HashMap<>();
        for (Node node : nodes) {
            visitedEdgesMap.put(node, new ArrayList<>());
        }
        
        // Find a starting node for the path
        Node startNode = findStartNodeDirected(nodes);
        if (startNode == null) {
            // If no suitable starting node is found, return an empty path
            return path;
        }
        
        // Initialize a stack to track the current path
        LinkedList<Node> stack = new LinkedList<>();
        stack.push(startNode);
        
        while (!stack.isEmpty()) {
            Node currentNode = stack.peek();
            List<Edge> edgesFromCurrentNode = currentNode.getEdges();
            boolean foundNextEdge = false;
            
            for (Edge edge : edgesFromCurrentNode) {
                if (remainingEdges.contains(edge) && !visitedEdgesMap.get(currentNode).contains(edge)) {
                    // Remove the edge from the remaining edges list
                    remainingEdges.remove(edge);
                    
                    // Mark the edge as visited
                    visitedEdgesMap.get(currentNode).add(edge);
                    
                    // Push the end node of the edge onto the stack
                    stack.push(edge.getEnd());
                    
                    foundNextEdge = true;
                    break;
                }
            }
            
            if (!foundNextEdge) {
                // If no valid edge is found, add the current node to the path
                path.add(0, stack.pop());
                
                // Check if there are remaining edges and the current node has no outgoing edges
                if (!remainingEdges.isEmpty() && currentNode.getEdges().isEmpty()) {
                    // Find a new starting node with remaining edges
                    Node newStartNode = findStartNodeWithRemainingEdges(nodes, visitedEdgesMap);
                    if (newStartNode != null) {
                        // Push the new starting node onto the stack
                        stack.push(newStartNode);
                    }
                }
            }
        }
        
        // Check if all edges have been visited
        if (remainingEdges.isEmpty()) {
            return path;
        } else {
            // If there are remaining edges, the graph is not connected, return an empty path
            return new ArrayList<>();
        }
    }
    
    // Helper method to find a suitable start node for the Eulerian path
    private static Node findStartNodeDirected(List<Node> nodes) {
        Node startNode = null;
        for (Node node : nodes) {
            int outDegree = node.getEdges().size();
            int inDegree = 0;
            for (Edge edge : node.getEdges()) {
                if (edge.getEnd() == node) {
                    inDegree++;
                }
            }
            if (outDegree - inDegree == 1 || inDegree - outDegree == 1) {
                startNode = node;
                break;
            }
        }
        return startNode;
    }
    
    // Helper method to find a new starting node with remaining edges
    private static Node findStartNodeWithRemainingEdges(List<Node> nodes, Map<Node, List<Edge>> visitedEdgesMap) {
        for (Node node : nodes) {
            if (!visitedEdgesMap.get(node).isEmpty()) {
                for (Edge edge : node.getEdges()) {
                    if (!visitedEdgesMap.get(node).contains(edge)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }
    
    public static List<Node> fleuryDirected(List<Node> nodes, List<Edge> edges) {
        List<Node> path = new ArrayList<>();

        // Check if the graph is Eulerian or semi-Eulerian
        if (!isEulerianDirected(nodes, edges) && !isSemiEulerianDirected(nodes, edges)) {
            // If the graph is not Eulerian or semi-Eulerian, return an empty path
            return path;
        }

        // Initialize a map to track visited edges for each node
        Map<Node, List<Edge>> visitedEdgesMap = new HashMap<>();
        for (Node node : nodes) {
            visitedEdgesMap.put(node, new ArrayList<>());
        }

        // Find a starting node for the path
        Node startNode = findStartNode(nodes);
        if (startNode == null) {
            // If no suitable starting node is found, return an empty path
            return path;
        }

        // Perform depth-first search to find the Eulerian path
        dfsDirected(startNode, path, visitedEdgesMap);

        return path;
    }

    // Depth-first search to find Eulerian path
    private static void dfsDirected(Node currentNode, List<Node> path, Map<Node, List<Edge>> visitedEdgesMap) {
        List<Edge> edges = currentNode.getEdges();

        for (Edge edge : edges) {
            if (!visitedEdgesMap.get(currentNode).contains(edge)) {
                visitedEdgesMap.get(currentNode).add(edge);

                Node nextNode = edge.getEnd();

                // Recur for the next node
                dfsDirected(nextNode, path, visitedEdgesMap);
            }
        }

        // Add the current node to the path
        path.add(0, currentNode);
    }
    
    
 // Dijkstra's Algorithm to find the shortest path between nodes in a weighted graph
    public static Map<Node, Integer> dijkstra(List<Node> nodes, Node source) {
        Map<Node, Integer> distances = new HashMap<>();
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(source, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(source);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Edge edge : current.getEdges()) {
                Node neighbor = edge.getEnd();
                int newDistance = distances.get(current) + edge.getWeight();
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }


    // Bellman-Ford Algorithm to compute shortest paths from a single source, allowing for negative weights
    public static Map<Node, Integer> bellmanFord(List<Node> nodes, List<Edge> edges, Node source) {
        Map<Node, Integer> distances = new HashMap<>();
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(source, 0);

        for (int i = 0; i < nodes.size() - 1; i++) {
            for (Edge edge : edges) {
                Node u = edge.getStart();
                Node v = edge.getEnd();
                int weight = edge.getWeight();
                if (distances.get(u) != Integer.MAX_VALUE && distances.get(u) + weight < distances.get(v)) {
                    distances.put(v, distances.get(u) + weight);
                }
            }
        }

        for (Edge edge : edges) {
            Node u = edge.getStart();
            Node v = edge.getEnd();
            int weight = edge.getWeight();
            if (distances.get(u) != Integer.MAX_VALUE && distances.get(u) + weight < distances.get(v)) {
                throw new IllegalStateException("Graph contains a negative cycle");
            }
        }

        return distances;
    }
    
    
 // Transitive Closure Algorithm to compute the transitive closure of a graph
    public static boolean[][] transitiveClosure(List<Node> nodes, List<Edge> edges) {
        int size = nodes.size();
        boolean[][] closure = new boolean[size][size];

        // Initialize closure matrix based on direct connections
        for (Edge edge : edges) {
            Node start = edge.getStart();
            Node end = edge.getEnd();
            closure[nodes.indexOf(start)][nodes.indexOf(end)] = true;
        }

        // Floyd-Warshall algorithm for computing transitive closure
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    closure[i][j] = closure[i][j] || (closure[i][k] && closure[k][j]);
                }
            }
        }

        return closure;
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
	
	public static List<Node> Dantzigs(List<Node> nodes, Node source, Node destination) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialize distances
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(source, 0);
        pq.offer(source);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current == destination) break;

            for (Edge edge : current.getEdges()) {
                Node neighbor = edge.getEnd();
                int newDistance = distances.get(current) + edge.getWeight();
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    pq.offer(neighbor);
                }
            }
        }

        List<Node> shortestPath = new ArrayList<>();
        Node current = destination;
        while (current != null) {
            shortestPath.add(current);
            current = previous.get(current);
        }
        Collections.reverse(shortestPath);

        return shortestPath;
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