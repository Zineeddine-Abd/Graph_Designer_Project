package GraphDesigner;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;

public class GraphConfigurationDialog extends JDialog {
    
    private boolean directed;
    private boolean weighted;
    private boolean useMatrix;
    private boolean loaded = false;
    

    public GraphConfigurationDialog() {
        setTitle("Graph Configuration");
        setSize(600, 350); // Increased size for better layout
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); // Added some spacing between components
        
        Font TitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 30); // Increased font size for labels
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 22); // Increased font size for labels
        Font checkBoxFont = new Font(Font.SANS_SERIF, Font.PLAIN, 25); // Increased font size for checkboxes
        
        JLabel configurationLabel = new JLabel("<html><u>PreConfigurations:</u></html>");
        configurationLabel.setFont(TitleFont);
        panel.add(configurationLabel);

        JLabel NBLabel = new JLabel("<html><u>(Check the box if yes)</u></html>");
        NBLabel.setFont(labelFont);
        panel.add(NBLabel);
        
        JLabel directedLabel = new JLabel("Directed Graph:");
        directedLabel.setFont(labelFont);
        panel.add(directedLabel);

        JCheckBox directedCheckBox = new JCheckBox();
        directedCheckBox.setFont(checkBoxFont);
        panel.add(directedCheckBox);

        JLabel weightedLabel = new JLabel("Weighted Graph:");
        weightedLabel.setFont(labelFont);
        panel.add(weightedLabel);

        JCheckBox weightedCheckBox = new JCheckBox();
        weightedCheckBox.setFont(checkBoxFont);
        panel.add(weightedCheckBox);

        JLabel matrixLabel = new JLabel("Use Adjacency Matrix:");
        matrixLabel.setFont(labelFont);
        panel.add(matrixLabel);

        JCheckBox matrixCheckBox = new JCheckBox();
        matrixCheckBox.setFont(checkBoxFont);
        panel.add(matrixCheckBox);

        JLabel loadGraphLabel = new JLabel("Load graph from file:");
        loadGraphLabel.setFont(labelFont);
        panel.add(loadGraphLabel);

        JButton loadGraphButton = new JButton("LOAD GRAPH");
        loadGraphButton.setFont(labelFont); // Increased font size for buttons
        loadGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGraphFromFile();
            }
        });
        panel.add(loadGraphButton);

        JButton saveButton = new JButton("Create Graph");
        saveButton.setFont(labelFont); // Increased font size for buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                directed = directedCheckBox.isSelected();
                weighted = weightedCheckBox.isSelected();
                useMatrix = matrixCheckBox.isSelected();
                dispose();
            }
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }
   
    
    private void loadGraphFromFile() {
    	
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this); //thi = parent component (JDialog) for centering the Filechooser
        
        if (option == JFileChooser.APPROVE_OPTION) { // file has been selected
        	
        	loaded = true;
            File file = fileChooser.getSelectedFile();
            
            try (Scanner scanner = new Scanner(file)) {
            	
                // Read graph information from the file
                int numNodes = Integer.parseInt(scanner.nextLine());
                List<Node> loadedNodes = new ArrayList<>();
                for (int i = 0; i < numNodes; i++) {
                    String[] nodeInfo = scanner.nextLine().split(",");
                    int x = Integer.parseInt(nodeInfo[0]);
                    int y = Integer.parseInt(nodeInfo[1]);
                    String nodeName = nodeInfo[2];
                    loadedNodes.add(new Node(new Point(x, y), nodeName));
                }
                
                int numEdges = Integer.parseInt(scanner.nextLine());
                List<Edge> loadedEdges = new ArrayList<>();
                for (int i = 0; i < numEdges; i++) {
                    String[] edgeInfo = scanner.nextLine().split(",");
                    int startIndex = Integer.parseInt(edgeInfo[0]);
                    int endIndex = Integer.parseInt(edgeInfo[1]);
                    int weight = Integer.parseInt(edgeInfo[2]);
                    loadedEdges.add(new Edge(loadedNodes.get(startIndex), loadedNodes.get(endIndex), weight));
                }
                boolean loadedDirected = Boolean.parseBoolean(scanner.nextLine());
                boolean loadedWeighted = Boolean.parseBoolean(scanner.nextLine());

                // Create a new GraphDesigner with loaded graph information
                GraphDesigner graphDesigner = new GraphDesigner(loadedDirected, loadedWeighted);
                graphDesigner.getGraphPanel().getNodes().addAll(loadedNodes);
                graphDesigner.getGraphPanel().getEdges().addAll(loadedEdges);
                graphDesigner.getGraphPanel().fillNodesNeighbors();
                graphDesigner.getGraphPanel().repaint();
                graphDesigner.setVisible(true);
                
                // Close the configuration dialog
                dispose();
             
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public boolean isDirected() {
        return directed;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public boolean isUseMatrix() {
        return useMatrix; 
    }
    
    public boolean isLoaded() {
        return loaded; 
    }
    
    
}


