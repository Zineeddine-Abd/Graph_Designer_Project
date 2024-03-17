import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TreePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Map<String, TreeNode> nodesMap;
    private JButton addButton;
    private JButton removeButton;
    private JButton addRootButton;
    private JButton saveButton;
    private JButton loadButton;
    private JLabel nodeName = new JLabel("Node Name : ");
    private JLabel parentNodeName = new JLabel("Parent Node Name : ");
    private JTextField nodeNameField;
    private JTextField parentNodeField;

    // Node colors
    private Color nodeColor = Color.BLUE;
    private Color textColor = Color.WHITE; // Change text color to black
    private Font nodeFont = new Font("Arial", Font.PLAIN, 14);
    private int nodeNameFontSize = 14;

    // Adjustable border thickness
    private int nodeBorderThickness = 2;
    private int edgeThickness = 2;

    // Variables for dragging nodes
    private boolean isDragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private TreeNode draggedNode;

    // Radius of the node
    private int nodeRadius = 20;

    JPanel leftPanel;
    JPanel rightPanel;

    public TreePanel() {

        nodesMap = new HashMap<>();
        addButton = new JButton("Add Node");
        removeButton = new JButton("Remove Node");
        addRootButton = new JButton("Add Root Node");
        saveButton = new JButton("Save Tree");
        loadButton = new JButton("Load Tree");
        nodeNameField = new JTextField(20);
        parentNodeField = new JTextField(20);
        setLayout(new BorderLayout());

        // left Panel----------------------------------------------
        leftPanel = new JPanel();

        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setBorder(new LineBorder(Color.BLACK));

        leftPanel.setPreferredSize(new Dimension(370, getHeight()));

        JLabel titleLabel = new JLabel("TREE CONFIGURATIONS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(1, 1, 368, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBackground(Color.GRAY); // Set the background color to yellow
        titleLabel.setOpaque(true); // Make sure to setOpaque(true) to make the background color visible
        titleLabel.setBorder(new LineBorder(Color.BLUE));
        leftPanel.add(titleLabel);

        nodeName.setBounds(20, 70, 120, 20);
        nodeNameField.setBounds(200, 70, 150, 20);
        parentNodeName.setBounds(20, 110, 120, 20);
        parentNodeField.setBounds(200, 110, 150, 20);

        addButton = new JButton("Add Node");
        addButton.setBorder(new LineBorder(Color.BLACK));
        addButton.setBounds(20, 160, 150, 30);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nodeName = nodeNameField.getText().trim();
                String parentNodeName = parentNodeField.getText().trim();
                addNode(nodeName, parentNodeName);
            }
        });

        removeButton = new JButton("Remove Node");
        removeButton.setBorder(new LineBorder(Color.BLACK));
        removeButton.setBounds(200, 160, 150, 30);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nodeName = nodeNameField.getText().trim();
                removeNode(nodeName);
                if (nodesMap.isEmpty()) {
                    addRootButton.setEnabled(true);
                }
            }
        });

        addRootButton = new JButton("Add Root Node");
        addRootButton.setBorder(new LineBorder(Color.BLACK));
        addRootButton.setBounds(20, 210, 150, 30);
        addRootButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRootNode();
                addRootButton.setEnabled(false);
            }
        });

        saveButton = new JButton("Save Tree");
        Font saveButtonFont = new Font("Arial", Font.BOLD, 15);
        saveButton.setFont(saveButtonFont);
        saveButton.setBackground(Color.BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(new LineBorder(Color.BLACK));
        saveButton.setBounds(10, 720, 350, 50);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTree();
            }
        });

        loadButton = new JButton("Load Tree");
        Font loadButtonFont = new Font("Arial", Font.BOLD, 15);
        loadButton.setFont(loadButtonFont);
        loadButton.setBorder(new LineBorder(Color.BLUE));
        loadButton.setBounds(10, 650, 350, 50);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTree();
            }
        });
        
        // Add label for maximizing frame
        JLabel maximizeLabel = new JLabel("<html><div style='text-align: center;'>Please maximize the window for better experience</div></html>");
        maximizeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        maximizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        maximizeLabel.setBounds(10, 550, 350, 70);
        // Set foreground color (text color) to red
        maximizeLabel.setForeground(Color.RED);
        // Set border to black
        maximizeLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        
        // Add label for designer information
        JLabel designerLabel = new JLabel("Designed by Zine eddine ABDELADIM");
        designerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        designerLabel.setBounds(10, 750, 350, 70);
        designerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        designerLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        leftPanel.add(designerLabel);
        leftPanel.add(maximizeLabel);
        leftPanel.add(addRootButton);
        leftPanel.add(addButton);
        leftPanel.add(removeButton);
        leftPanel.add(nodeName);
        leftPanel.add(nodeNameField);
        leftPanel.add(parentNodeName);
        leftPanel.add(parentNodeField);
        leftPanel.add(saveButton);
        leftPanel.add(loadButton);

        add(leftPanel, BorderLayout.WEST);

        // right Panel----------------------------------------------------

        rightPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!nodesMap.isEmpty()) {
                    for (TreeNode node : nodesMap.values()) {
                        drawTree(g, node);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int maxX = 0;
                int maxY = 0;
                for (TreeNode node : nodesMap.values()) {
                    if (node.x > maxX) {
                        maxX = node.x;
                    }
                    if (node.y > maxY) {
                        maxY = node.y;
                    }
                }
                // Adding some padding to maxX and maxY to ensure all nodes are visible
                maxX += nodeRadius + 50; // Adjust padding as needed
                maxY += nodeRadius + 50; // Adjust padding as needed
                return new Dimension(maxX, maxY);
            }

        };

        // Set layout to null for manual positioning
        rightPanel.setLayout(null);

        // Add mouse listeners for dragging nodes
        rightPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                draggedNode = findNodeAtPosition(mouseX, mouseY);
                if (draggedNode != null) {
                    isDragging = true;
                    dragOffsetX = mouseX - draggedNode.x;
                    dragOffsetY = mouseY - draggedNode.y;
                }
            }

            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (draggedNode != null) {
                    draggedNode = null;
                    repaint();
                }
            }
        });

        rightPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isDragging && draggedNode != null) {
                    // Calculate the new position of the dragged node
                    int x = e.getX() - dragOffsetX;
                    int y = draggedNode.y;

                    // Ensure the node stays within the bounds of the panel
                    if (x < nodeRadius) {
                        x = nodeRadius;
                    } else if (x > rightPanel.getWidth() - nodeRadius) {
                        x = rightPanel.getWidth() - nodeRadius;
                    }

                    // Update the node's position
                    draggedNode.x = x;
                    draggedNode.y = y;
                    repaint();
                }
            }
        });

        // Create a JScrollPane and add rightPanel to it
        JScrollPane scrollPane = new JScrollPane(rightPanel);
        scrollPane.setBorder(new LineBorder(Color.BLACK));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add the JScrollPane to the center of TreePanel
        add(scrollPane, BorderLayout.CENTER);
    }

    private TreeNode findNodeAtPosition(int x, int y) {
        for (TreeNode node : nodesMap.values()) {
            int nodeX = node.x - nodeRadius;
            int nodeY = node.y - nodeRadius;
            int width = 2 * nodeRadius;
            int height = 2 * nodeRadius;
            if (x >= nodeX && x <= nodeX + width && y >= nodeY && y <= nodeY + height) {
                return node;
            }
        }
        return null;
    }

    private void addRootNode() {
        String nodeName = nodeNameField.getText().trim();
        if (!nodeName.isEmpty()) {
            if (!nodesMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Root node already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                addNode(nodeName, null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a node name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addNode(String nodeName, String parentNodeName) {
        if (nodesMap.containsKey(nodeName)) {
            JOptionPane.showMessageDialog(this, "Node with the same name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TreeNode newNode = new TreeNode(nodeName);
        if (parentNodeName == null) {
            if (!nodesMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Root node already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            newNode.x = rightPanel.getWidth() / 2;
            newNode.y = 90;
            nodesMap.put(nodeName, newNode);
        } else {
            TreeNode parentNode = nodesMap.get(parentNodeName);
            if (parentNode == null) {
                JOptionPane.showMessageDialog(this, "Parent node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculate x-coordinate for new node based on the parent node's position
            int numChildren = parentNode.children.size();
            int siblingWidth = 100; // Adjust as needed
            int totalWidth = siblingWidth * (numChildren + 1);
            newNode.x = parentNode.x - totalWidth / 2 + (numChildren + 1) * siblingWidth;
            newNode.y = parentNode.y + 100; // Adjust y-coordinate for new node
            parentNode.addChild(newNode);
            nodesMap.put(nodeName, newNode);
        }
        revalidate(); // Update the preferred size
        repaint();
    }


    public void removeNode(String nodeName) {
        TreeNode nodeToRemove = nodesMap.get(nodeName);
        if (nodeToRemove == null) {
            JOptionPane.showMessageDialog(this, "Node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Remove the node and its children recursively
        removeNodeAndChildren(nodeToRemove);
        revalidate(); // Update the preferred size
        repaint();
    }

    private void removeNodeAndChildren(TreeNode node) {
        // Recursively remove children
        for (TreeNode child : new ArrayList<>(node.children)) {
            removeNodeAndChildren(child);
        }
        // Remove the node from its parent's children list
        TreeNode parentNode = findParentNode(node);
        if (parentNode != null) {
            parentNode.removeChild(node);
        }
        // Remove the node from the nodesMap
        nodesMap.remove(node.name);
    }

    private TreeNode findParentNode(TreeNode node) {
        for (TreeNode parent : nodesMap.values()) {
            if (parent.children.contains(node)) {
                return parent;
            }
        }
        return null;
    }

    private void saveTree() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                outputStream.writeObject(nodesMap);
                JOptionPane.showMessageDialog(this, "Tree saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving tree: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTree() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                nodesMap = (Map<String, TreeNode>) inputStream.readObject();
                revalidate(); // Update the preferred size
                repaint();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading tree: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void drawTree(Graphics g, TreeNode node) {
        Graphics2D g2d = (Graphics2D) g;

        // Store the default stroke
        Stroke defaultStroke = g2d.getStroke();

        // Set the desired thickness for the edges
        float edgeThickness = 2.0f; // Adjust the thickness as needed

        // Create a stroke with the desired thickness
        Stroke edgeStroke = new BasicStroke(edgeThickness);

        // Set border thickness for nodes
        g2d.setStroke(edgeStroke);

        // Draw edges
        for (TreeNode child : node.children) {
            // Calculate start and end points of the edge
            int startX = node.x;
            int startY = node.y;
            int endX = child.x;
            int endY = child.y;

            // Calculate the angle between the two points
            double angle = Math.atan2(endY - startY, endX - startX);

            // Adjust start and end points to the borders of the nodes
            startX += (int) (Math.cos(angle) * nodeRadius);
            startY += (int) (Math.sin(angle) * nodeRadius);
            endX -= (int) (Math.cos(angle) * nodeRadius);
            endY -= (int) (Math.sin(angle) * nodeRadius);

            // Draw the edge
            g.setColor(Color.BLACK);
            g.drawLine(startX, startY, endX, endY);

            // Recursively draw children
            drawTree(g, child);
        }

        // Draw current node after drawing edges to ensure it appears on top of edges
        // Draw current node
        g.setColor(Color.BLACK);
        g.drawOval(node.x - nodeRadius, node.y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.setColor(Color.BLUE);
        g.fillOval(node.x - nodeRadius, node.y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Adjust font size based on node radius
        Font boldFont = nodeFont.deriveFont(Font.BOLD, nodeNameFontSize);

        FontMetrics fm = g.getFontMetrics(boldFont);
        int nameWidth = fm.stringWidth(node.name);
        int nameHeight = fm.getHeight();
        int nameX = node.x - nameWidth / 2;
        int nameY = node.y + nameHeight / 4;

        g.setColor(textColor);
        g.setFont(boldFont);
        g.drawString(node.name, nameX, nameY);

        // Revert to default stroke
        g2d.setStroke(defaultStroke);

        // Recursively draw children nodes
        for (TreeNode child : node.children) {
            drawTree(g, child);
        }
    }

    private static class TreeNode implements Serializable {
        String name;
        int x;
        int y;
        java.util.List<TreeNode> children;

        TreeNode(String name) {
            this.name = name;
            children = new ArrayList<>();
        }

        void addChild(TreeNode child) {
            children.add(child);
        }

        void removeChild(TreeNode child) {
            children.remove(child);
        }
    }
}
