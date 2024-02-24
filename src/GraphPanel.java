import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private enum Mode { ADD_NODE, ADD_EDGE }

    private List<Point> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    
    private Point selectedNode1 = null;
    private Point selectedNode2 = null;
    
    private Mode mode = Mode.ADD_NODE;
    
    private boolean directed = false;
    private boolean weighted = false;
    
    private JLabel weightLabel;
    private JTextField weightTextField;
    private boolean weightEntered = false;

    public GraphPanel(boolean directed, boolean weighted) {
        this.directed = directed;
        this.weighted = weighted;

        setLayout(null);

        JButton addNodeButton = new JButton("Add Node");
        addNodeButton.setBounds(10, 20, 100, 30);
        addNodeButton.addActionListener(e -> {
            mode = Mode.ADD_NODE;
            repaint();
        });
        add(addNodeButton);

        JButton addEdgeButton = new JButton("Add Edge");
        addEdgeButton.setBounds(120, 20, 100, 30);
        addEdgeButton.addActionListener(e -> {
            mode = Mode.ADD_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            if (weighted) {
                weightLabel.setVisible(true);
                weightTextField.setVisible(true);
                weightTextField.requestFocus();
            }
            repaint();
        });
        add(addEdgeButton);

        if (weighted) {
            weightLabel = new JLabel("Weight:");
            weightLabel.setBounds(230, 20, 60, 30);
            weightLabel.setVisible(false);
            add(weightLabel);

            weightTextField = new JTextField();
            weightTextField.setBounds(290, 20, 50, 30);
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
                switch (mode) {
                    case ADD_NODE:
                        addNode(clickedPoint);
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
                }
            }
        });
    }

    private void addNode(Point point) {
        nodes.add(point);
        repaint();
    }

    private void selectNodesForEdge(Point point) {
        for (Point node : nodes) {
            if (node.distance(point) < 10) {
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

    // Method to create graph from adjacency matrix
    public void createGraphFromAdjacencyMatrix(int[][] adjacencyMatrix) {
    	
        nodes.clear();
        edges.clear();
        
        int numNodes = adjacencyMatrix.length;

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Point((i + 1) * 50, 200));
        }

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j), adjacencyMatrix[i][j]));
                }
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw nodes
        int nodeSize = 20;
        for (Point node : nodes) {
            g.setColor(Color.BLUE);
            g.fillOval(node.x - nodeSize / 2, node.y - nodeSize / 2, nodeSize, nodeSize);
        }

        // Draw edges
        for (Edge edge : edges) {
            g.setColor(Color.BLACK);
            g.drawLine(edge.getStart().x, edge.getStart().y, edge.getEnd().x, edge.getEnd().y);
            if (weighted) {
                g.drawString(String.valueOf(edge.getWeight()), (edge.getStart().x + edge.getEnd().x) / 2, (edge.getStart().y + edge.getEnd().y) / 2);
            }
            if (directed) {
                drawArrow(g, edge.getStart(), edge.getEnd());
            }
        }
        // Draw selected nodes for edge creation
        if (selectedNode1 != null) {
            g.setColor(Color.RED);
            g.fillOval(selectedNode1.x - 5, selectedNode1.y - 5, 10, 10);
        }
        if (selectedNode2 != null) {
            g.setColor(Color.RED);
            g.fillOval(selectedNode2.x - 5, selectedNode2.y - 5, 10, 10);
        }
    }

    private void drawArrow(Graphics g, Point start, Point end) {
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        int arrowSize = 10;
        int x1 = (int) (end.x - arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (end.y - arrowSize * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (end.x - arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (end.y - arrowSize * Math.sin(angle + Math.PI / 6));
        g.drawLine(end.x, end.y, x1, y1);
        g.drawLine(end.x, end.y, x2, y2);
    }

    private static class Edge {
        private final Point start;
        private final Point end;
        private final int weight;

        public Edge(Point start, Point end) {
            this.start = start;
            this.end = end;
            this.weight = 1;
        }

        public Edge(Point start, Point end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }

        public Point getStart() {
            return start;
        }

        public Point getEnd() {
            return end;
        }

        public int getWeight() {
            return weight;
        }
    }
}
