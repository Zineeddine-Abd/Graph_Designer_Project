
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StructureConfigurationDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JButton graphButton;
    private JButton treeButton;

    private boolean isGraphSelected = false;

    public StructureConfigurationDialog() {
    	
        setTitle("Select Data Structure");
        setSize(400, 250);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
    	
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        graphButton = new JButton("Graph");
        graphButton.addActionListener(new ActionListener() { //  an anonymous class that implements the ActionListener interface is created
            @Override
            public void actionPerformed(ActionEvent e) {
                isGraphSelected = true;
                dispose();
            }
        });

        treeButton = new JButton("Tree");
        treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGraphSelected = false;
                dispose();
            }
        });

        panel.add(graphButton);
        panel.add(treeButton);

        add(panel, BorderLayout.CENTER);
    }

    public boolean isGraphSelected() {
        return isGraphSelected;
    }
}
