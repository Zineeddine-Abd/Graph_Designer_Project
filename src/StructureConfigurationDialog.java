
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StructureConfigurationDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JButton graphButton;
    private JButton treeButton;

    private boolean isGraphSelected = false;
    private boolean isTreeSelected = false;

    public StructureConfigurationDialog() {
    	
        setTitle("Select a designer");
        setSize(400, 250);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
    	
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        graphButton = new JButton("Graph Designer");
        // Set the font size of the button text
        Font graphFont = graphButton.getFont();
        graphButton.setFont(graphFont.deriveFont(Font.BOLD, 20));
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGraphSelected = true;
                dispose();
            }
        });

        treeButton = new JButton("Tree Designer");
        // Set the font size of the button text
        Font treeFont = treeButton.getFont();
        treeButton.setFont(treeFont.deriveFont(Font.BOLD, 20));
        treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTreeSelected = true;
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
    
    public boolean isTreeSelected() {
    	return isTreeSelected;
    }
}
