import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;



public class GraphPanel extends JPanel {
	
	//Program modes
    private enum Mode { ADD_NODE, ADD_EDGE, REMOVE_NODE, REMOVE_EDGE }
    private Mode mode = Mode.ADD_NODE;
    
    //Graph infos
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private boolean directed = false;
    private boolean weighted = false;
    
    //For adding and deleting nodes and edges
    private Node selectedNode1 = null;
    private Node selectedNode2 = null;
    private Edge selectedEdge = null;
    
    private Node selectedNode = null; // Track the selected node for dragging
    private Point lastMousePosition = null; // Track the last mouse position for dragging

    private JLabel weightLabel;
    private boolean weightEntered = false;
    private JTextField weightTextField;
 
    private JButton addNodeButton;
    private JButton removeNodeButton;
    private JTextField nodeNameTextField;
    private boolean addNodeClicked = false;
    
    JButton addEdgeButton;
    private JButton removeEdgeButton;
    
    private JButton generateMatrixButton;


    public GraphPanel(boolean directed, boolean weighted) {
    	
        this.directed = directed;
        this.weighted = weighted;

        setLayout(null);

        addNodeButton = new JButton("Add Node");
        addNodeButton.setBounds(10, 20, 120, 30);
        addNodeButton.setEnabled(false);
        addNodeButton.addActionListener(e -> {
            mode = Mode.ADD_NODE;
            addNodeClicked = true;
            repaint();
        });
        add(addNodeButton);
        
        removeNodeButton = new JButton("Remove Node");
        removeNodeButton.setBounds(10, 60, 120, 30);
        removeNodeButton.addActionListener(e -> {
            mode = Mode.REMOVE_NODE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;
            repaint();
        });
        add(removeNodeButton);

        addEdgeButton = new JButton("Add Edge");
        addEdgeButton.setBounds(140, 20, 120, 30);
        addEdgeButton.addActionListener(e -> {
            mode = Mode.ADD_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;

            if (weighted) {
                weightLabel.setVisible(true);
                weightTextField.setVisible(true);
                weightTextField.requestFocus();
            }
            repaint();
        });
        add(addEdgeButton);
        
        removeEdgeButton = new JButton("Remove Edge"); // New button for removing edges
        removeEdgeButton.setBounds(140, 60, 120, 30);
        removeEdgeButton.addActionListener(e -> {
            mode = Mode.REMOVE_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;
            repaint();
        });
        add(removeEdgeButton);
        
        generateMatrixButton = new JButton("Generate Adjacency Matrix");
        generateMatrixButton.setBounds(10, 110, 200, 30);
        generateMatrixButton.addActionListener(e -> {
            generateAndDisplayAdjacencyMatrix();
        });
        add(generateMatrixButton);
        
        JButton saveGraphButton = new JButton("Save Graph");
        saveGraphButton.setBounds(10, 150, 200, 30);
        saveGraphButton.addActionListener(e -> saveGraphToFile());
        add(saveGraphButton);
    	

        JLabel nodeNameLabel = new JLabel("Node Name:");
        nodeNameLabel.setBounds(290, 20, 80, 30);
        add(nodeNameLabel);

        nodeNameTextField = new JTextField();
        nodeNameTextField.setBounds(390, 20, 100, 30);
        nodeNameTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkNodeName();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkNodeName();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkNodeName();
            }
        });
        add(nodeNameTextField);

        if (weighted) {
            weightLabel = new JLabel("Weight:");
            weightLabel.setBounds(520, 20, 60, 30);
            weightLabel.setVisible(false);
            add(weightLabel);

            weightTextField = new JTextField();
            weightTextField.setBounds(590, 20, 50, 30);
            weightTextField.setVisible(false);
            weightTextField.addActionListener(e -> {
                weightEntered = true;
                addEdge();
            });
            add(weightTextField);
        }
        
        
        

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point clickedPoint = e.getPoint();
                if(e.getY() > 190) {
	                switch (mode) {
	                    case ADD_NODE:
	                        if (!nodeNameTextField.getText().isEmpty()) {
	                            addNode(clickedPoint);
	                        }
	                        break;
	                    case REMOVE_NODE:
	                        removeNode(clickedPoint);
	                        break;
	                    case ADD_EDGE:
	                        if (weighted) {
	                            if (weightEntered) {
	                                selectNodesForEdge(clickedPoint);
	                            }
	                        } else {
	                            selectNodesForEdge(clickedPoint);
	                        }
	                        break;
	                    case REMOVE_EDGE:
	                        removeEdge(clickedPoint);
	                        break;
	                }
                }
            }

        });
        
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (selectedNode != null) {
                    Point currentMousePosition = e.getPoint();
                    int dx = currentMousePosition.x - lastMousePosition.x;
                    int dy = currentMousePosition.y - lastMousePosition.y;
                    selectedNode.moveBy(dx, dy);
                    lastMousePosition = currentMousePosition;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point clickedPoint = e.getPoint();
                for (Node node : nodes) {
                    if (node.contains(clickedPoint)) {
                        selectedNode = node;
                        lastMousePosition = clickedPoint;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                selectedNode = null;
            }
        });
 
    }
    
    
    private void saveGraphToFile() {
        // Prompt user to choose a file location
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
            	
                // Write graph information to the file
                writer.println(nodes.size()); // Number of nodes
                for (Node node : nodes) {
                    writer.println(node.getPoint().x + "," + node.getPoint().y + "," + node.getNodeName());
                }
                
                writer.println(edges.size()); // Number of edges
                for (Edge edge : edges) {
                    writer.println(nodes.indexOf(edge.getStart()) + "," + nodes.indexOf(edge.getEnd()) + "," + edge.getWeight());
                }
                
                writer.println(directed); // Whether the graph is directed
                writer.println(weighted); // Whether the graph is weighted
                writer.println(); // Add a blank line for readability
                
                JOptionPane.showMessageDialog(this, "The graph is saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save the graph.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNode(Point point) {
        String nodeName = nodeNameTextField.getText();
        Node newNode = new Node(point, nodeName);
        nodes.add(newNode);
        nodeNameTextField.setText("");
        checkNodeName();
        repaint();
    }
    
    private void removeNode(Point point) {
        for (Node node : nodes) {
            if (node.contains(point)) {
                nodes.remove(node);
                removeIncidentEdges(node);
                repaint();
                return;
            }
        }
    }

    private void removeIncidentEdges(Node node) {
        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getStart().equals(node) || edge.getEnd().equals(node)) {
                edgesToRemove.add(edge);
            }
        }
        edges.removeAll(edgesToRemove);
    }

    private void selectNodesForEdge(Point point) {
        for (Node node : nodes) {
            if (node.contains(point)) {
                if (selectedNode1 == null) {
                    selectedNode1 = node;
                } else if (selectedNode2 == null && !selectedNode1.equals(node)) {
                    selectedNode2 = node;
                    if (weighted) {
                        String weightStr = weightTextField.getText();
                        int weight = 1;
                        try {
                            weight = Integer.parseInt(weightStr);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        edges.add(new Edge(selectedNode1, selectedNode2, weight));
                        weightTextField.setText("");
                        weightLabel.setVisible(false);
                        weightTextField.setVisible(false);
                        weightEntered = false;
                    } else {
                        edges.add(new Edge(selectedNode1, selectedNode2));
                    }
                    selectedNode1 = null;
                    selectedNode2 = null;
                    repaint();
                    return;
                }
            }
        }
    }

    private void addEdge() {
        if (selectedNode1 != null && selectedNode2 != null && !weightEntered) {
            String weightStr = weightTextField.getText();
            int weight = 1;
            try {
                weight = Integer.parseInt(weightStr);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            edges.add(new Edge(selectedNode1, selectedNode2, weight));
            selectedNode1 = null;
            selectedNode2 = null;
            weightTextField.setText("");
            weightLabel.setVisible(false);
            weightTextField.setVisible(false);
            repaint();
        }
    }
    
    private void removeEdge(Point clickPoint) {
        for (Edge edge : edges) {
            if (edge.isPointNearLine(clickPoint)) {
                selectedEdge = edge;
                edges.remove(edge);
                repaint();
                break;
            }
        }
    }

    private void checkNodeName() {
    	String nodeName = nodeNameTextField.getText();
        boolean nameTaken = false;
        
        // Check if the entered node name is already taken by another node
        for (Node node : nodes) {
            if (node.getNodeName().equals(nodeName)) {
                nameTaken = true;
                break;
            }
        }
        
        // Enable/disable the "Add Node" button based on whether the name is taken
        addNodeButton.setEnabled(!nodeName.isEmpty() && !nameTaken);
    }
    
 // Method to create graph from adjacency matrix with distributed nodes and node names and edge weights
    public void createGraphFromAdjacencyMatrix(int[][] adjacencyMatrix, String[] nodeNames, String[] edgeWeights) {
        nodes.clear();
        edges.clear();
   
        int numNodes = adjacencyMatrix.length;

        // Calculate center of the panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Calculate the maximum distance from the center to the edge of the panel
        int maxDistance = Math.min(centerX, centerY) * 2 / 5; // Adjust the maximum distance as needed

        // Calculate the angle between nodes
        double angleIncrement = 2 * Math.PI / numNodes;

        // Calculate the position of each node and create nodes
        for (int i = 0; i < numNodes; i++) {
            double angle = angleIncrement * i;
            int x = (int) (centerX + maxDistance * Math.cos(angle));
            int y = (int) (centerY + maxDistance * Math.sin(angle));
            nodes.add(new Node(new Point(x, y), nodeNames[i]));
        }

        // Create edges based on the adjacency matrix and edge weights
        int index = 0;
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    int weight = Integer.parseInt(edgeWeights[index]);
                    edges.add(new Edge(nodes.get(i), nodes.get(j), weight));
                    index++;
                }
            }
        }

        repaint();
    }

    // Method to create graph from adjacency matrix with distributed nodes and node names
    public void createGraphFromAdjacencyMatrix(int[][] adjacencyMatrix, String[] nodeNames) {
        nodes.clear();
        edges.clear();

        int numNodes = adjacencyMatrix.length;

        // Calculate center of the panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Calculate the maximum distance from the center to the edge of the panel
        int maxDistance = Math.min(centerX, centerY) * 2 / 5; // Adjust the maximum distance as needed

        // Calculate the angle between nodes
        double angleIncrement = 2 * Math.PI / numNodes;

        // Calculate the position of each node and create nodes
        for (int i = 0; i < numNodes; i++) {
            double angle = angleIncrement * i;
            int x = (int) (centerX + maxDistance * Math.cos(angle));
            int y = (int) (centerY + maxDistance * Math.sin(angle));
            nodes.add(new Node(new Point(x, y), nodeNames[i]));
        }

        // Create edges based on the adjacency matrix
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j)));
                }
            }
        }

        repaint();
    }
    
    
    private void generateAndDisplayAdjacencyMatrix() {
        int numNodes = nodes.size();
        int[][] adjacencyMatrix = new int[numNodes][numNodes];

        // Populate the adjacency matrix based on the edges
        for (Edge edge : edges) {
            int startIndex = nodes.indexOf(edge.getStart());
            int endIndex = nodes.indexOf(edge.getEnd());
            adjacencyMatrix[startIndex][endIndex] = 1;
            if (!directed) {
                adjacencyMatrix[endIndex][startIndex] = 1;
            }
        }

        // Display the adjacency matrix
        StringBuilder matrixText = new StringBuilder("Adjacency Matrix:\n");
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                matrixText.append(adjacencyMatrix[i][j]).append(" ");
            }
            matrixText.append("\n");
        }
        JOptionPane.showMessageDialog(this, matrixText.toString(), "Adjacency Matrix", JOptionPane.PLAIN_MESSAGE);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Edge edge : edges) {
            edge.draw(g, weighted, directed);
        }

        for (Node node : nodes) {
            node.draw(g);
        }

        if (selectedNode1 != null) {
            g.setColor(Color.RED);
            g.fillOval(selectedNode1.getPoint().x - 5, selectedNode1.getPoint().y - 5, 10, 10);
        }
        if (selectedNode2 != null) {
            g.setColor(Color.RED);
            g.fillOval(selectedNode2.getPoint().x - 5, selectedNode2.getPoint().y - 5, 10, 10);
        }
    }


    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }
}

class Node {
    private Point point;
    private String nodeName;
    private final Color color = Color.RED;
    private final int size = 30;

    public Node(Point point, String nodeName) {
        this.point = point;
        this.nodeName = nodeName;
        
    }

    public Point getPoint() {
        return point;
    }

    public String getNodeName() {
        return nodeName;
    }
    

    public boolean contains(Point p) {
        int tolerance = 10; // Adjust tolerance as needed
        return (Math.abs(point.x - p.x) <= tolerance && Math.abs(point.y - p.y) <= tolerance);
    }
    
    public void moveBy(int dx, int dy) {
        point.translate(dx, dy);
    }

    public void draw(Graphics g) {
        int nodeSize = size;
        // Change font size and style for node name
        Font font = new Font("Arial", Font.BOLD, 13);
        g.setFont(font);
        
        g.setColor(color);
        g.fillOval(point.x - nodeSize / 2, point.y - nodeSize / 2, nodeSize, nodeSize);
        if (nodeName != null) {
            g.setColor(Color.BLACK);
            g.drawString(nodeName, point.x - nodeName.length() * 3, point.y + 5);
        }
    }
}

class Edge {
    private Node start;
    private Node end;
    private int weight;
    private final Color color = Color.BLACK;
    private final int thickness = 4;

    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
        this.weight = 1; // Default weight == 1
    }

    public Edge(Node start, Node end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }
    
    Node getStart() {
    	return start;
    }
    
    Node getEnd() {
    	return end;
    }
    
    int getWeight() {
    	return weight;
    }
    
    public boolean isPointNearLine(Point point) {
        // Calculate the distance from the point to the line segment defined by the edge
        double distance = Line2D.ptSegDist(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y, point.x, point.y);
        
        // Define a threshold distance within which we consider the point to be "near" the line
        double threshold = 5.0; // Adjust this threshold as needed
        
        // Return true if the distance is within the threshold
        return distance <= threshold;
    }
    
    public void draw(Graphics g, boolean weighted, boolean directed) {
        
    	g.setColor(color);
        
    	//----------------------------------------
        Graphics2D g2d = (Graphics2D) g;

        // Save the original stroke
        Stroke originalStroke = g2d.getStroke();

        // Set the custom stroke thickness
        g2d.setStroke(new BasicStroke(thickness));

        // Draw the edge
        g2d.setColor(color);
        g2d.drawLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        
        // Restore the original stroke
        g2d.setStroke(originalStroke);
        //----------------------------------------
        
        g.drawLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        if (weighted) {
        	// Draw weight in a square at the midpoint of the edge
            int midX = (start.getPoint().x + end.getPoint().x) / 2;
            int midY = (start.getPoint().y + end.getPoint().y) / 2;
            int squareSize = 20; // Size of the square for displaying weight
            int x = midX - squareSize / 2;
            int y = midY - squareSize / 2;
            
            g.setColor(Color.WHITE);
            g.drawRect(x, y, squareSize, squareSize);
            g.fillRect(x, y, squareSize, squareSize);
            
            // Draw weight inside the square
            g.setColor(Color.BLACK);
            Font font = new Font("Arial", Font.BOLD, 12); // Adjust font size and style as needed
            g.setFont(font);
            g.drawString(String.valueOf(weight), midX - 5, midY + 5); // Adjust position of the weight text
            
        }
        if (directed) {
            drawArrow(g2d, start.getPoint(), end.getPoint());
        }
    }

    private void drawArrow(Graphics g, Point start, Point end) {
        Graphics2D g2d = (Graphics2D) g;

        // Calculate the angle of the edge
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        // Calculate the distance from the end point to the border of the node
        int arrowSize = Math.max(10, thickness * 2); // Adjust the arrow size based on edge thickness

        // Calculate the intersection point between the edge line and the border of the node
        double nodeRadius = Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
        int intersectionX = end.x - (int) (arrowSize * (end.x - start.x) / nodeRadius);
        int intersectionY = end.y - (int) (arrowSize * (end.y - start.y) / nodeRadius);

        // Calculate the length of the arrow (increase by 10 pixels)
        int arrowLength = arrowSize + 10;

        // Calculate the arrow endpoints
        int x1 = intersectionX - (int) (arrowLength * Math.cos(angle - Math.PI / 6));
        int y1 = intersectionY - (int) (arrowLength * Math.sin(angle - Math.PI / 6));
        int x2 = intersectionX - (int) (arrowLength * Math.cos(angle + Math.PI / 6));
        int y2 = intersectionY - (int) (arrowLength * Math.sin(angle + Math.PI / 6));

        // Save the original stroke
        Stroke originalStroke = g2d.getStroke();

        // Set the custom stroke thickness
        g2d.setStroke(new BasicStroke(thickness));

        // Draw the arrow lines
        g2d.drawLine(intersectionX, intersectionY, x1, y1);
        g2d.drawLine(intersectionX, intersectionY, x2, y2);

        // Restore the original stroke
        g2d.setStroke(originalStroke);
    }


}

