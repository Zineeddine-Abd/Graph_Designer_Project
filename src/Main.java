import javax.swing.*;
import java.awt.*;

public class Main {
	
    public static void main(String[] args) {
        // Create and display the configuration dialog
        SwingUtilities.invokeLater(() -> {
            GraphConfigurationDialog dialog = new GraphConfigurationDialog();
            centerFrame(dialog); // Center the dialog
            dialog.setVisible(true);

            boolean directed = dialog.isDirected();
            boolean weighted = dialog.isWeighted();
            dialog.dispose(); // Dispose the dialog after retrieving configuration

            // Create and display the main frame
            SwingUtilities.invokeLater(() -> {
                GraphDesigner graphDesigner = new GraphDesigner(directed, weighted);
                centerFrame(graphDesigner); // Center the main frame
                graphDesigner.setVisible(true);
            });
        });
    }

    // Method to center a Window (JFrame, JDialog, etc.) on the screen
    private static void centerFrame(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;
        window.setLocation(x, y);
    }
}
