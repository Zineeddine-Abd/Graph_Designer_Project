import javax.swing.*;

import javax.swing.*;

import javax.swing.*;

public class GraphDesigner extends JFrame {

    public GraphDesigner(boolean directed, boolean weighted) {
        setTitle("Graph Designer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphPanel graphPanel = new GraphPanel(directed, weighted);
        add(graphPanel);

        setVisible(true);
    }

    // Updated constructor to accommodate adjacency matrix
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix) {
        setTitle("Graph Designer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphPanel graphPanel = new GraphPanel(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix); // Create graph from adjacency matrix
        add(graphPanel);

        setVisible(true);
    }
}
