package GraphDesigner;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import GraphAlgorithms.Algorithms;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class GraphPanel extends JPanel {
	
	// Program modes
    private enum Mode { ADD_NODE, ADD_EDGE, REMOVE_NODE, REMOVE_EDGE }
    private Mode mode = Mode.ADD_NODE;
    
    JPanel rightPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    
    // Graph Infos
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private boolean directed = false;
    private boolean weighted = false;

    // For adding and deleting nodes and edges
    private Node selectedNode1 = null;
    private Node selectedNode2 = null;
    private Edge selectedEdge = null;
    
    // Track the selected node for dragging and the last mouse position for dragging
    private Node selectedNode = null;
    private Point lastMousePosition = null; 
    
    //Weight entries
    private JLabel weightLabel;
    private JTextField weightTextField;
    private boolean weightEntered = false;
    
    //add node buttons
    private JButton addNodeButton;
    private JButton removeNodeButton;
    private JTextField nodeNameTextField;
    private boolean addNodeClicked = false;
    
    //remove node buttons
    private JButton addEdgeButton;
    private JButton removeEdgeButton;
    
    //generate matrix button
    private JButton generateMatrixButton;
    
    //Algorithms variables
    private JComboBox<String> algorithmDropdown;
    private JButton executeButton;
    
    //Screen shot variables
    private BufferedImage offScreenImage;  // Off-screen image for buffering


    public GraphPanel(boolean directed, boolean weighted) {
    	
        this.directed = directed;
        this.weighted = weighted;

        setLayout(new BorderLayout());

        // Left panel for buttons and input fields---------------------------------
        JPanel leftPanel = new JPanel();
        
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setBorder(new LineBorder(Color.BLACK));
        
        leftPanel.setPreferredSize(new Dimension(370, getHeight()));
        add(leftPanel, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("GRAPH CONFIGURATIONS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(1, 1, 368, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        titleLabel.setForeground(Color.	WHITE);
        titleLabel.setBackground(Color.GRAY); // Set the background color to yellow
        titleLabel.setOpaque(true); // Make sure to setOpaque(true) to make the background color visible
        titleLabel.setBorder(new LineBorder(Color.BLUE));
        leftPanel.add(titleLabel);

        // Add buttons and input fields to the left panel
        addNodeButton = new JButton("Add Node");
        addNodeButton.setBorder(new LineBorder(Color.BLACK));
        addNodeButton.setBounds(10, 70, 120, 30);
        addNodeButton.setEnabled(false);
        addNodeButton.addActionListener(e -> { // The e parameter represents the ActionEvent object
            mode = Mode.ADD_NODE;			   //that is passed to the actionPerformed method when the button is clicked
            addNodeClicked = true;
            repaint();
        });
        leftPanel.add(addNodeButton);

        removeNodeButton = new JButton("Remove Node");
        removeNodeButton.setBorder(new LineBorder(Color.BLACK));
        removeNodeButton.setBounds(10, 110, 120, 30);
        removeNodeButton.addActionListener(e -> {
            mode = Mode.REMOVE_NODE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;
            repaint();
        });
        leftPanel.add(removeNodeButton);
        
        addEdgeButton = new JButton("Add Edge");
        addEdgeButton.setBorder(new LineBorder(Color.BLACK));
        addEdgeButton.setBounds(10, 170, 120, 30);
        addEdgeButton.addActionListener(e -> {
            mode = Mode.ADD_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;

            if (weighted) {
                weightLabel.setVisible(true);
                weightTextField.setVisible(true);
                weightTextField.requestFocus(); // the component that has focus typically displays a highlighted border or some other visual indicator
            }                                   // to indicate that it's the currently active component.
            repaint();
        });
        leftPanel.add(addEdgeButton);

        removeEdgeButton = new JButton("Remove Edge");
        removeEdgeButton.setBorder(new LineBorder(Color.BLACK));
        removeEdgeButton.setBounds(10, 210, 120, 30);
        removeEdgeButton.addActionListener(e -> {
            mode = Mode.REMOVE_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            selectedEdge = null;
            repaint();
        });
        leftPanel.add(removeEdgeButton);
        
        //Graph Algorithms label
        JLabel algorithmsLabel = new JLabel("GRAPH ALGORITHMS");
        algorithmsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        algorithmsLabel.setBounds(1, 270, 368, 50);
        algorithmsLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        algorithmsLabel.setForeground(Color.WHITE);
        algorithmsLabel.setBackground(Color.GRAY); // Set the background color to yellow
        algorithmsLabel.setOpaque(true); // Make sure to setOpaque(true) to make the background color visible
        algorithmsLabel.setBorder(new LineBorder(Color.BLUE));
        leftPanel.add(algorithmsLabel);
        
        //Dropdown menu for algorithm selection
        String[] algorithms = {"Check Hamiltonian", "Check Eulerian", "Check Semi-Eulerian", "Hierholzer's Algorithm" , "Fleury's Algorithm" , "Depth-First Search" , "Breadth-First Search"};
        algorithmDropdown = new JComboBox<>(algorithms);
        algorithmDropdown.setBorder(new LineBorder(Color.BLACK));
        algorithmDropdown.setBounds(10, 350, 160, 30);
        leftPanel.add(algorithmDropdown);
        
        // Button to execute the selected algorithm
        executeButton = new JButton("Execute");
        executeButton.setBorder(new LineBorder(Color.BLACK));
        executeButton.setBounds(190, 350, 160, 30);
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeAlgorithm((String) algorithmDropdown.getSelectedItem());
            }
        });
        leftPanel.add(executeButton);
        
        JButton loadGraphButton = new JButton("Load Graph");
        Font loadButtonFont = new Font("Arial", Font.BOLD, 20);
        loadGraphButton.setFont(loadButtonFont);
        loadGraphButton.setBorder(new LineBorder(Color.BLUE));
        
        loadGraphButton.setBounds(10, 500, 350, 50);
        loadGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGraphFromFile();
            }
        });
        leftPanel.add(loadGraphButton);
    
        JButton ScreenshotButton = new JButton("Take Screenshot");
        ScreenshotButton.setBounds(10, 570, 350, 50);
        Font screenShotButtonFont = new Font("Arial", Font.BOLD, 20);
        ScreenshotButton.setFont(screenShotButtonFont);
        ScreenshotButton.setBorder(new LineBorder(Color.BLUE));
        ScreenshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGraphImage();
            }
        });
        leftPanel.add(ScreenshotButton);
        
        
        generateMatrixButton = new JButton("Generate Adjacency Matrix");
        Font matrixButtonFont = new Font("Arial", Font.BOLD, 20);
        generateMatrixButton.setFont(matrixButtonFont);
        generateMatrixButton.setBorder(new LineBorder(Color.BLUE));
        
        generateMatrixButton.setBounds(10, 640, 350, 50);
        generateMatrixButton.addActionListener(e -> {
            generateAndDisplayAdjacencyMatrix();
        });
        leftPanel.add(generateMatrixButton);

        JButton saveGraphButton = new JButton("Save Graph");
        Font saveButtonFont = new Font("Arial", Font.BOLD, 20);
        saveGraphButton.setFont(saveButtonFont);
        saveGraphButton.setBackground(Color.BLUE);
        saveGraphButton.setForeground(Color.WHITE);
        saveGraphButton.setBorder(new LineBorder(Color.BLACK));
        
        saveGraphButton.setBounds(10, 720, 350, 50);
        saveGraphButton.addActionListener(e -> saveGraphToFile());
        leftPanel.add(saveGraphButton);

        JLabel nodeNameLabel = new JLabel("Node Name:");
        nodeNameLabel.setBounds(150, 70, 80, 30);
        leftPanel.add(nodeNameLabel);
        
        //adding listeners to node name textfeild
        nodeNameTextField = new JTextField();
        nodeNameTextField.setBounds(240, 70, 120, 30);
        nodeNameTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() { //anonymous inner class
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
        leftPanel.add(nodeNameTextField);

        if (weighted) {
            weightLabel = new JLabel("Edge Weight:");
            weightLabel.setBounds(150, 170, 80, 30);
            weightLabel.setVisible(false);
            leftPanel.add(weightLabel);

            weightTextField = new JTextField();
            weightTextField.setBounds(240, 170, 50, 30);
            weightTextField.setVisible(false);
            weightTextField.addActionListener(e -> {
                weightEntered = true;
                addEdge();
            });
            leftPanel.add(weightTextField);
        }
        
        // Add label for maximizing frame
        JLabel maximizeLabel = new JLabel("<html><div style='text-align: center;'>You should maximize the window for better experience</div></html>");
        maximizeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        maximizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        maximizeLabel.setBounds(10, 400, 350, 70);
        // Set foreground color (text color) to red
        maximizeLabel.setForeground(Color.RED);
        // Set border to black
        maximizeLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        
        // Add label for designer information
        JLabel designerLabel = new JLabel("Designed by Zine eddine ABDELADIM");
        designerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        designerLabel.setBounds(10, 750, 290, 70);
        designerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        designerLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        leftPanel.add(designerLabel);
        leftPanel.add(maximizeLabel);

        // Right panel for drawing the graph--------------------------------------
        JPanel rightPanel = new JPanel() {
        	
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Edge edge : edges) {
                    edge.draw(g, weighted, directed);
                }
                for (Node node : nodes) {
                    node.draw(g);
                }
                
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, getHeight());
            }
        };
        
        rightPanel.setBackground(new Color(255,255,255));
        rightPanel.setBorder(new LineBorder(Color.BLACK));
        rightPanel.setLayout(new BorderLayout());
        add(rightPanel, BorderLayout.CENTER);

        rightPanel.addMouseListener(new MouseAdapter() { //MouseAdapter class in Java is an abstract adapter class that implements the MouseListener interface.
            @Override                                    //It provides default implementations for all the methods in the MouseListener interface
            public void mouseClicked(MouseEvent e) {	 //so i can implement just the methods i want to implement
                
            	super.mouseClicked(e);
                Point clickedPoint = e.getPoint();
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
        });

        rightPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (selectedNode != null) {
                    Point currentMousePosition = e.getPoint();
                    // Check if the dragging point is within the bounds of the right panel
                    if (rightPanel.contains(currentMousePosition)) {
                        int dx = currentMousePosition.x - lastMousePosition.x;
                        int dy = currentMousePosition.y - lastMousePosition.y;
                        selectedNode.moveBy(dx, dy);
                        lastMousePosition = currentMousePosition;
                        rightPanel.repaint();
                    }
                }
            }
        });


        rightPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);// its empty method , but for no errors
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
    
    
    //Main methods
    private void executeAlgorithm(String algorithmName) {
        switch (algorithmName) {
            case "Check Hamiltonian":
                if (!nodes.isEmpty()) {
                    boolean isHamiltonian = Algorithms.isHamiltonian(nodes, edges);
                    if (isHamiltonian) {
                        JOptionPane.showMessageDialog(this, "The graph is Hamiltonian.", "Hamiltonian Graph", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "The graph is not Hamiltonian.", "Hamiltonian Graph", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Check Eulerian":
                boolean isEulerian;
                if (!nodes.isEmpty()) {
                    if (directed) {
                        isEulerian = Algorithms.isEulerianDirected(nodes);
                    } else {
                        isEulerian = Algorithms.isEulerian(nodes);
                    }
                    if (isEulerian) {
                        JOptionPane.showMessageDialog(this, "The graph is Eulerian.", "Eulerian Graph", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "The graph is not Eulerian.", "Eulerian Graph", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Check Semi-Eulerian":
                boolean isSemiEulerian;
                if (!nodes.isEmpty()) {
                    if (directed) {
                        isSemiEulerian = Algorithms.isSemiEulerianDirected(nodes);
                    } else {
                        isSemiEulerian = Algorithms.isSemiEulerian(nodes);
                    }
                    if (isSemiEulerian) {
                        JOptionPane.showMessageDialog(this, "The graph is Semi Eulerian.", "Semi Eulerian Graph", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "The graph is not Semi Eulerian.", "Semi Eulerian Graph", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Hierholzer's Algorithm":
                List<Node> circuit = new ArrayList<>();
                if (!nodes.isEmpty()) {
                    if (directed) {
                        circuit = Algorithms.hierholzerEulerianPathDirected(nodes, edges);
                    } else {
                        circuit = Algorithms.hierholzerEulerianPath(nodes);
                    }
                    fillNodesNeighbors();
                    if (circuit != null) {
                        displayResult("Hierholzer's Algorithm", circuit);
                    } else {
                        JOptionPane.showMessageDialog(this, "the graph doesn't contains any circuits or paths.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Fleury's Algorithm":
                if (!nodes.isEmpty()) {
                    List<Node> path = Algorithms.fleuryEulerianPath(nodes);
                    fillNodesNeighbors();
                    displayResult("Fleury's Algorithm", path);
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Find Flows":
                if (!nodes.isEmpty()) {
                    List<List<Node>> flows = Algorithms.findFlows(nodes, edges);
                    StringBuilder message = new StringBuilder("Flows in the graph:\n");
                    for (List<Node> flow : flows) {
                        message.append(flow).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, message.toString(), "Graph Flows", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Depth-First Search":
                Node startNodeDFS = nodes.isEmpty() ? null : nodes.get(0);
                if (startNodeDFS != null) {
                    List<Node> dfsResult = Algorithms.dfs(startNodeDFS);
                    StringBuilder messageDFS = new StringBuilder("Depth-First Search result:\n");
                    for (Node node : dfsResult) {
                        messageDFS.append(node).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, messageDFS.toString(), "DFS Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "Breadth-First Search":
                Node startNodeBFS = nodes.isEmpty() ? null : nodes.get(0);
                if (startNodeBFS != null) {
                    List<Node> bfsResult = Algorithms.bfs(startNodeBFS);
                    StringBuilder messageBFS = new StringBuilder("Breadth-First Search result:\n");
                    for (Node node : bfsResult) {
                        messageBFS.append(node).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, messageBFS.toString(), "BFS Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No nodes in the graph.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default:
                System.out.println("Algorithm not found.");
        }
    }

    
    private void displayResult(String algorithmName, List<Node> result) {
        StringBuilder message = new StringBuilder();
        message.append("Result of ").append(algorithmName).append(":\n");
        for (Node node : result) {
            message.append(node.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, message.toString());
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
                
                writer.println(edges.size()); // Number edges
                for (Edge edge : edges) {
                    writer.println(nodes.indexOf(edge.getStart()) + "," + nodes.indexOf(edge.getEnd()) + "," + edge.getWeight());
                }
                
                writer.println(directed); 
                writer.println(weighted); 
                writer.println(); 
                
                JOptionPane.showMessageDialog(this, "The graph is saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save the graph.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void fillNodesNeighbors() {
    	
    	//List<Edge> edgesToAdd = new ArrayList<>();
    	for (Node node : nodes) {
    		
    		for (Edge edge : edges) {
    			if(edge.getStart() == node) {
    				node.getEdges().add(edge);
    				//edgesToAdd.add(edge);
    			}
    		}
    		//node.setEdges(edgesToAdd);
    	}
    }
    
    
    private void loadGraphFromFile() {
        // Open file chooser dialog to select the file
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Scanner scanner = new Scanner(selectedFile);
                
                getNodes().clear();
                getEdges().clear();
                
                // Read the number of nodes
                int numNodes = Integer.parseInt(scanner.nextLine());
                
                // Read node coordinates and names
                for (int i = 0; i < numNodes; i++) {
                    String[] nodeData = scanner.nextLine().split(",");
                    int x = Integer.parseInt(nodeData[0]);
                    int y = Integer.parseInt(nodeData[1]);
                    String nodeName = nodeData[2];
                    // Create node and add to graph panel
                    Node newNode = new Node(new Point(x, y), nodeName);
                    getNodes().add(newNode);
                    
                }
                
                // Read the number of edges
                int numEdges = Integer.parseInt(scanner.nextLine());
                
                // Read edge data
                for (int i = 0; i < numEdges; i++) {
                    String[] edgeData = scanner.nextLine().split(",");
                    int startIndex = Integer.parseInt(edgeData[0]);
                    int endIndex = Integer.parseInt(edgeData[1]);
                    int weight = Integer.parseInt(edgeData[2]);
                    // Create edge and add to graph panel
                    Edge newEdge = new Edge(getNodes().get(startIndex), getNodes().get(endIndex), weight);
                    getEdges().add(newEdge);
                }
                
                fillNodesNeighbors();
                
                Boolean Directed = Boolean.parseBoolean(scanner.nextLine());
                Boolean Weighted = Boolean.parseBoolean(scanner.nextLine());
                
                // Create a new GraphDesigner with loaded graph information
                GraphDesigner graphDesigner = new GraphDesigner(Directed, Weighted);
                graphDesigner.getGraphPanel().getNodes().addAll(nodes);
                graphDesigner.getGraphPanel().getEdges().addAll(edges);
                graphDesigner.getGraphPanel().repaint();
                graphDesigner.setVisible(true);
                // Close the current frame
                SwingUtilities.getWindowAncestor(this).dispose();
                
                
                // Close the scanner
                scanner.close();
                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load graph from file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveGraphImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Graph As Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                rightPanel.paint(g2d);
                g2d.dispose();

                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Graph saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving graph: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void removeIncidentEdges(Node nodeToRemove) {
        List<Edge> edgesToRemove = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getStart().equals(nodeToRemove) || edge.getEnd().equals(nodeToRemove)) {
                edgesToRemove.add(edge);
            }
        }
        edges.removeAll(edgesToRemove);
        
        edgesToRemove.clear();
        for (Node node : nodes) {
        	for(Edge edge : node.getEdges()) {
	            if (edge.getStart().equals(nodeToRemove) || edge.getEnd().equals(nodeToRemove)) {
	                edgesToRemove.add(edge);
	            }
        	}
        	node.getEdges().removeAll(edgesToRemove);
        }
        
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

                        Edge edge = new Edge(selectedNode1, selectedNode2, weight);
                        edges.add(edge);
                        selectedNode1.addEdge(edge);
                        if (!directed) {
        	                // If the graph is undirected, add the reverse edge
        	                Edge reverseEdge = new Edge(selectedNode2, selectedNode1, weight);
        	                selectedNode2.addEdge(reverseEdge);
        	                edges.add(reverseEdge);
        	            }
                        weightTextField.setText("");
                        weightLabel.setVisible(false);
                        weightTextField.setVisible(false);
                        weightEntered = false;
                        
                    } else {

                    	Edge edge = new Edge(selectedNode1, selectedNode2);
                        edges.add(edge);
                        selectedNode1.addEdge(edge);
                        if (!directed) {
        	                // If the graph is undirected, add the reverse edge
        	                Edge reverseEdge = new Edge(selectedNode2, selectedNode1);
        	                selectedNode2.addEdge(reverseEdge);
        	                edges.add(reverseEdge);
        	            }
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
            Edge edge = new Edge(selectedNode1, selectedNode2, weight);
            edges.add(edge);
            selectedNode1.addEdge(edge);
            if (!directed) {
                // If the graph is undirected, add the reverse edge
                Edge reverseEdge = new Edge(selectedNode2, selectedNode1, weight);
                selectedNode2.addEdge(reverseEdge);
                edges.add(reverseEdge);
            }
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
                selectedEdge.getStart().getEdges().remove(edge);
                if (!directed) {
    	            // If the graph is undirected, remove the reverse edge
    	            Node destination = edge.getEnd();
    	            // crate the reverse edge temporarly to search for it
    	            Edge reverseEdge = new Edge(destination, edge.getStart(), edge.getWeight());
    	            destination.getEdges().remove(reverseEdge);
    	            edges.remove(reverseEdge);
    	        }
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

    
    //Getters and Setters
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

