import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphConfigurationDialog extends JDialog {
	
    private boolean directed;
    private boolean weighted;

    public GraphConfigurationDialog() {
    	
        setTitle("Graph Configuration");
        setSize(300, 150);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel directedLabel = new JLabel("Directed:");
        panel.add(directedLabel);

        JCheckBox directedCheckBox = new JCheckBox();
        panel.add(directedCheckBox);

        JLabel weightedLabel = new JLabel("Weighted:");
        panel.add(weightedLabel);

        JCheckBox weightedCheckBox = new JCheckBox();
        panel.add(weightedCheckBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                directed = directedCheckBox.isSelected();
                weighted = weightedCheckBox.isSelected();
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
}
