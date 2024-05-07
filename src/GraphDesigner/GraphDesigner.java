package GraphDesigner;

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
	
	private GraphPanel graphPanel;
	
	
	//---------------------------------------------------------------------------------------------------------------------------------
	//for simple initialization
	
    public GraphDesigner(boolean directed, boolean weighted) {
        initialize(directed, weighted);
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    //for matrix initialization
    
    //Weighted
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames, String[] edgeWeights) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames, edgeWeights);
    }
    
    //UnWeighted
    public GraphDesigner(boolean directed, boolean weighted, int[][] adjacencyMatrix, String[] nodeNames) {
        initialize(directed, weighted);
        graphPanel.createGraphFromAdjacencyMatrix(adjacencyMatrix, nodeNames);
    }
    //---------------------------------------------------------------------------------------------------------------------------------

    //initialize the frame of the graph designer
    private void initialize(boolean directed, boolean weighted) {
    	
        setTitle("Graph Designer");
        // Get the screen size
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Set the size of the frame to match the screen size
        setSize(1700, 900);
        
        // Load the icon from the resources directory
        ImageIcon icon = new ImageIcon(getClass().getResource("/imgs/Graph_Icon.png"));
        // Set the icon image
        setIconImage(icon.getImage());

        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        graphPanel = new GraphPanel(directed, weighted);
        add(graphPanel);

        setVisible(true); // Make the frame visible before calling createGraphFromAdjacencyMatrix
    }

    
    
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
