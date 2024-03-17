import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> { //(for he safety)
        
        	
        	// Create and display the structure configuration dialog
            StructureConfigurationDialog structureDialog = new StructureConfigurationDialog();
            centerFrame(structureDialog); // Center the dialog
            structureDialog.setVisible(true);
            
            // Wait until the structure dialog is disposed
            structureDialog.addWindowListener(new java.awt.event.WindowAdapter() {
	            @Override
	            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
		            boolean isGraphSelected = structureDialog.isGraphSelected();
		            boolean isTreeSelected = structureDialog.isTreeSelected();
		            structureDialog.dispose(); // Dispose the dialog after retrieving configuration
		        	
		            if(isGraphSelected) {
			        	// Create and display the Graph configuration dialog
			            GraphConfigurationDialog graphDialog = new GraphConfigurationDialog();
			            centerFrame(graphDialog); // Center the dialog
			            graphDialog.setVisible(true);
			
			            boolean directed = graphDialog.isDirected();
			            boolean weighted = graphDialog.isWeighted();
			            boolean useMatrix = graphDialog.isUseMatrix();
			            graphDialog.dispose(); // Dispose the dialog after retrieving configuration
			
			            if (useMatrix) {
			                // Show dialog to input adjacency matrix and node names
			                JTextField dimensionField = new JTextField(5);
			                JTextField matrixField = new JTextField(20);
			                JTextField nodeNameField = new JTextField(20);
			                JTextField weightField = null;
			
			                JPanel matrixPanel = new JPanel();
			                matrixPanel.setLayout(new BoxLayout(matrixPanel, BoxLayout.Y_AXIS));
			                matrixPanel.add(new JLabel("Matrix Dimension:"));
			                matrixPanel.add(dimensionField);
			                matrixPanel.add(Box.createVerticalStrut(10)); // it adds a vertical gap of height 10 pixels between the dimensionField and the previous component in the matrixPanel
			                matrixPanel.add(new JLabel("Adjacency Matrix (comma-separated):"));
			                matrixPanel.add(matrixField);
			                matrixPanel.add(Box.createVerticalStrut(10));
			                matrixPanel.add(new JLabel("Node Names (comma-separated):"));
			                matrixPanel.add(nodeNameField);
			
			                if (weighted) {
			                    weightField = new JTextField(20);
			                    matrixPanel.add(Box.createVerticalStrut(10));
			                    matrixPanel.add(new JLabel("Edge Weights (comma-separated):"));
			                    matrixPanel.add(weightField);
			                }
			
			                int result = JOptionPane.showConfirmDialog(null, matrixPanel,"Enter Adjacency Matrix and Node Names", JOptionPane.OK_CANCEL_OPTION);
			                
			                if (result == JOptionPane.OK_OPTION) {
			                    try {
			                        int dimension = Integer.parseInt(dimensionField.getText());
			                        String[] matrixValues = matrixField.getText().split(",");
			                        String[] nodeNames = nodeNameField.getText().split(","); // Get node names
			                        String[] edgeWeights = null;
			
			                        if (weighted) {
			                            edgeWeights = weightField.getText().split(","); // Get edge weights
			                        }
			
			                        int[][] adjacencyMatrix = new int[dimension][dimension];
			                        int index = 0;
			                        for (int i = 0; i < dimension; i++) {
			                            for (int j = 0; j < dimension; j++) {
			                                adjacencyMatrix[i][j] = Integer.parseInt(matrixValues[index]);
			                                index++;
			                            }
			                        }
			
			                        // Create and display the main frame with adjacency matrix
			                        final String[] finalEdgeWeights = edgeWeights; // Create final variable to hold edgeWeights
			                        SwingUtilities.invokeLater(() -> {
			                            GraphDesigner graphDesigner;
			                            if (weighted) {
			                                graphDesigner = new GraphDesigner(directed, weighted, adjacencyMatrix, nodeNames, finalEdgeWeights);
			                            } else {
			                                graphDesigner = new GraphDesigner(directed, weighted, adjacencyMatrix, nodeNames);
			                            }
			                            centerFrame(graphDesigner); // Center the main frame
			                            graphDesigner.setVisible(true);
			                        });
			
			                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid adjacency matrix, node names, and edge weights.");
			                    }
			                }
			            } else {
			            	if(!graphDialog.isLoaded()) {
				                // Proceed with manual graph creation
				                SwingUtilities.invokeLater(() -> {
				                    GraphDesigner graphDesigner = new GraphDesigner(directed, weighted);
				                    centerFrame(graphDesigner); // Center the main frame
				                    graphDesigner.setVisible(true);
				                });
			            	}
			            }
		            }
		            else {
		            	if(isTreeSelected) {
		            		// Display TreeDesigner frame when Tree structure is selected
			                SwingUtilities.invokeLater(() -> {
			                    TreeDesigner treeDesigner = new TreeDesigner();
			                    centerFrame(treeDesigner); // Center the main frame
			                    treeDesigner.setVisible(true);
			                });
		            	}
		            	else {
		            		
		            		//Nothing
		            	}
		            	
		            }
	            }
	            
            });
            
        });
    }

    // Method to center a Window (JFrame, JDialog, etc.) on the screen
    private static void centerFrame(java.awt.Window window) {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;
        window.setLocation(x, y);
    }
}
