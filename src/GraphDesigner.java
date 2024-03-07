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
	
	//---------------------------------------------------------------------------------------------------------------------------------
	//for simple initialization
    public GraphDesigner(boolean directed, boolean weighted) {
        initialize(directed, weighted);
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    //for matrix initialization
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames, String[] edgeWeights) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames, edgeWeights);
    }
    
    
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames); // Create graph from adjacency matrix
    }
    //---------------------------------------------------------------------------------------------------------------------------------

    //initialize the frame of the graph designer
    private void initialize(boolean directed, boolean weighted) {
        setTitle("Graph Designer");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        graphPanel = new GraphPanel(directed, weighted);
        add(graphPanel);

        setVisible(true); // Make the frame visible before calling createGraphFromAdjacencyMatrix
    }

    private GraphPanel graphPanel;
    
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
