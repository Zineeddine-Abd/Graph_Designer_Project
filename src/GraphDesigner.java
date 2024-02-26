import javax.swing.*;

import javax.swing.*;

import javax.swing.*;

import javax.swing.*;

import javax.swing.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphDesigner extends JFrame {

    public GraphDesigner(boolean directed, boolean weighted) {
        initialize(directed, weighted);
    }

    // Updated constructor to accommodate adjacency matrix, node names, and edge weights
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames, String[] edgeWeights) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames, edgeWeights); // Create graph from adjacency matrix
    }
    
    // Updated constructor to accommodate adjacency matrix, node names, and edge weights
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames); // Create graph from adjacency matrix
    }

    private void initialize(boolean directed, boolean weighted) {
        setTitle("Graph Designer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        graphPanel = new GraphPanel(directed, weighted);
        add(graphPanel);

        setVisible(true); // Make the frame visible before calling createGraphFromAdjacencyMatrix
    }

    private GraphPanel graphPanel;
}
