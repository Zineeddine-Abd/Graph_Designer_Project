package TreeDesigner;

import javax.swing.*;

public class TreeDesigner extends JFrame {

    private TreePanel treePanel;

    public TreeDesigner() {
        initialize();
    }

    private void initialize() {
        setTitle("Tree Designer");
        setSize(1700, 900);

        
        // Load the icon from the resources directory
        ImageIcon icon = new ImageIcon(getClass().getResource("/imgs/Tree_Icon.png"));
        // Set the icon image
        setIconImage(icon.getImage());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        treePanel = new TreePanel();
        add(treePanel);

        setVisible(true);
    }

    public TreePanel getTreePanel() {
        return treePanel;
    }
}
