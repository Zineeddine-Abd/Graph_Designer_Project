import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphConfigurationDialog extends JDialog {
    
    private boolean directed;
    private boolean weighted;
    private boolean useMatrix; // Add a boolean field to indicate whether to use adjacency matrix

    public GraphConfigurationDialog() {
        setTitle("Graph Configuration");
        setSize(300, 150);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2)); // Increase grid layout to accommodate new option

        JLabel directedLabel = new JLabel("Directed:");
        panel.add(directedLabel);

        JCheckBox directedCheckBox = new JCheckBox();
        panel.add(directedCheckBox);

        JLabel weightedLabel = new JLabel("Weighted:");
        panel.add(weightedLabel);

        JCheckBox weightedCheckBox = new JCheckBox();
        panel.add(weightedCheckBox);

        // Add checkbox for selecting whether to use adjacency matrix
        JLabel matrixLabel = new JLabel("Use Adjacency Matrix:");
        panel.add(matrixLabel);

        JCheckBox matrixCheckBox = new JCheckBox();
        panel.add(matrixCheckBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                directed = directedCheckBox.isSelected();
                weighted = weightedCheckBox.isSelected();
                useMatrix = matrixCheckBox.isSelected(); // Set the value of useMatrix
                dispose();
            }
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public boolean isUseMatrix() {
        return useMatrix; // Return the value of useMatrix
    }
}
