import javax.swing.*;

public class GraphDesigner extends JFrame {
	
    public GraphDesigner(boolean directed, boolean weighted) {
    	
        setTitle("Graph Designer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphPanel graphPanel = new GraphPanel(directed, weighted);
        add(graphPanel);

        setVisible(true);
    }
}
