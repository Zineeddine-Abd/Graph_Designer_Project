import javax.swing.*;
import java.awt.*;

public class TreeDesigner extends JFrame {

    private TreePanel treePanel;

    public TreeDesigner() {
        initialize();
    }

    private void initialize() {
        setTitle("Tree Designer");
        setSize(1700, 900);
        
        // Load the icon
        ImageIcon icon = new ImageIcon("Tree_Icon.png");
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
