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
	
	
	public static boolean isHamiltonianDirected(List<Node> nodes, List<Edge> edges) {
		
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
    
    public static boolean isEulerianDirected(List<Node> nodes) {
    	  // Check for strong connectedness (unchanged)
    	  if (!isStronglyConnected(nodes)) {
    	    return false;
    	  }

    	  // Check in-degree and out-degree equality
    	  for (Node node : nodes) {
    	    int inDegree = 0;
    	    int outDegree = node.getEdges().size(); // Outgoing edges stored in the node

    	    // Count in-degree by iterating through all nodes and their edges
    	    for (Node otherNode : nodes) {
    	      for (Edge edge : otherNode.getEdges()) {
    	        if (edge.getEnd() == node) {
    	          inDegree++;
    	          break; // Stop searching edges of the current otherNode once an incoming edge is found
    	        }
    	      }
    	    }

    	    if (inDegree != outDegree) {
    	      return false;
    	    }
    	  }

    	  // Eulerian graph confirmation (unchanged)
    	  return true;
    	}

    	// Helper method to check for strongly connected components using Kosaraju's Algorithm
    	private static boolean isStronglyConnected(List<Node> nodes) {
    	  // Perform DFS twice (forward and reverse) and check if all nodes are visited in both traversals
    	  Set<Node> visited1 = new HashSet<>();
    	  dfs(nodes.get(0), visited1);

    	  Set<Node> visited2 = new HashSet<>();
    	  for (Node node : nodes) {
    	    if (!visited1.contains(node)) {
    	      dfsReverse(node, visited2);
    	    }
    	  }

    	  // If all nodes are visited in both traversals, the graph is strongly connected
    	  return visited2.size() == nodes.size();
    	}

    	private static void dfs(Node node, Set<Node> visited) {
    	  visited.add(node);
    	  for (Edge edge : node.getEdges()) {
    	    if (!visited.contains(edge.getEnd())) {
    	      dfs(edge.getEnd(), visited);
    	    }
    	  }
    	}

    	private static void dfsReverse(Node node, Set<Node> visited) {
    	  visited.add(node);
    	  for (Node neighbor : getReverseNeighbors(node)) {
    	    if (!visited.contains(neighbor)) {
    	      dfsReverse(neighbor, visited);
    	    }
    	  }
    	}

    	private static List<Node> getReverseNeighbors(Node node) {
    	  List<Node> neighbors = new ArrayList<>();
    	  for (Edge edge : node.getEdges()) {
    	    if (edge.getEnd() == node) {
    	      neighbors.add(edge.getStart());
    	    }
    	  }
    	  return neighbors;
    	}
    
    	public static boolean isSemiEulerianDirected(List<Node> nodes) {
    		  int unbalancedNodes = 0;
    		  for (Node node : nodes) {
    		    int inDegree = 0;
    		    int outDegree = node.getEdges().size(); // Outgoing edges stored in the node

    		    // Count in-degree by iterating through all nodes and their edges
    		    for (Node otherNode : nodes) {
    		      for (Edge edge : otherNode.getEdges()) {
    		        if (edge.getEnd() == node) {
    		          inDegree++;
    		          break; // Stop searching edges of the current otherNode once an incoming edge is found
    		        }
    		      }
    		    }

    		    int difference = Math.abs(inDegree - outDegree);
    		    if (difference > 1) {
    		      return false;
    		    } else if (difference == 1) {
    		      unbalancedNodes++;
    		    }
    		  }

    		  // If there's only one unbalanced node, the graph is semi-Eulerian
    		  return unbalancedNodes == 2;
    		}
    
    
    	//hierholzer algorithm for directed graphs
    	public static List<Node> hierholzerDirected(List<Node> nodes) {

    	      // Early termination check for Hamiltonian cycle
    	      for (Node node : nodes) {
    	        int inDegree = 0;
    	        int outDegree = node.getEdges().size(); // Outgoing edges stored in the node

    	        // Count in-degree by iterating through all nodes and their edges
    	        for (Node otherNode : nodes) {
    	          for (Edge edge : otherNode.getEdges()) {
    	            if (edge.getEnd().getNodeName().equals(node.getNodeName())) {
    	              inDegree++;
    	              break; // Only count one incoming edge per node
    	            }
    	          }
    	        }

    	        if (inDegree != outDegree) {
    	          return null; // Not a Hamiltonian cycle (odd degree)
    	        }
    	      }

    	      // Find a suitable starting node
    	      Node startNode = null;
    	      for (Node node : nodes) {
    	        int inDegree = 0;
    	        int outDegree = node.getEdges().size(); // Outgoing edges stored in the node

    	        // ... (existing code to calculate in-degree)

    	        if (outDegree - inDegree == 1) {
    	          startNode = node;
    	          break;
    	        } else if (outDegree == inDegree) {
    	          startNode = node; // Choose any node if all in/out degrees are equal
    	        }
    	      }

    	      if (startNode == null) {
    	        return null; // No suitable starting node found
    	      }

    	      // Hierholzer's algorithm logic with limited stack and LinkedList for path
    	      Stack<Node> stack = new Stack<>(); // Limit stack size if needed
    	      Node current = startNode;
    	      List<Node> path = new LinkedList<>();
    	      while (true) {
    	        stack.push(current);
    	        path.add(current);

    	        List<Edge> outgoingEdges = new ArrayList<>(current.getEdges());
    	        boolean foundUnvisitedEdge = false;

    	        // Find an unvisited outgoing edge
    	        for (int i = outgoingEdges.size() - 1; i >= 0; i--) {
    	          Edge edge = outgoingEdges.get(i);
    	          if (!edge.getStart().equals(edge.getEnd())) { // Avoid self-loops
    	            current = edge.getEnd();
    	            outgoingEdges.remove(i);
    	            foundUnvisitedEdge = true;
    	            break;
    	          }
    	        }

    	        // If no unvisited outgoing edges are found, backtrack
    	        if (!foundUnvisitedEdge) {
    	          if (stack.isEmpty()) {
    	            break; // Reached starting node and exhausted all paths (circuit)
    	          }
    	          current = stack.pop();
    	        }
    	      }

    	      return path;
    	    }
    	  
    	

    
//    	public static List<Node> fleury(List<Node> nodes) {
//    	  // Choose any starting node
//    	  Node current = nodes.get(0);
//    	  List<Node> path = new ArrayList<>();
//
//    	  while (true) {
//    	    // Find any connected edge (assuming all edges are initially unvisited)
//    	    Edge nextEdge = null;
//    	    for (Edge edge : current.getEdges()) {
//    	      nextEdge = edge;
//    	      break;
//    	    }
//
//    	    // If no edges are found (all edges visited), the graph is Eulerian (entire path found)
//    	    if (nextEdge == null) {
//    	      path.add(current);
//    	      break;
//    	    }
//
//    	    // Add current node to the path
//    	    path.add(current);
//
//    	    // Remove the chosen edge from both nodes' edge lists (assuming the graph is bidirectional)
//    	    current.getEdges().remove(nextEdge);
//    	    nextEdge.getStart().getEdges().remove(nextEdge); // Assuming edge is bidirectional
//
//    	    // Update current node to the other end of the chosen edge
//    	    current = (current == nextEdge.getStart()) ? nextEdge.getEnd() : nextEdge.getStart();
//    	  }
//
//    	  return path;
//    	}
    	
    
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