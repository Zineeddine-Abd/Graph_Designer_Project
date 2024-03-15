import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TreePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Map<String, TreeNode> nodesMap;
    private JButton addButton;
    private JButton removeButton;
    private JButton addRootButton;
    private JButton saveButton; // Button to save the tree structure
    private JButton loadButton; // Button to load a saved tree structure
    private JTextField nodeNameField;
    private JTextField parentNodeField;

    public TreePanel() {
        nodesMap = new HashMap<>();
        addButton = new JButton("Add Node");
        removeButton = new JButton("Remove Node");
        addRootButton = new JButton("Add Root Node");
        saveButton = new JButton("Save Tree");
        loadButton = new JButton("Load Tree");
        nodeNameField = new JTextField(10);
        parentNodeField = new JTextField(10);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nodeName = nodeNameField.getText().trim();
                String parentNodeName = parentNodeField.getText().trim();
                addNode(nodeName, parentNodeName);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nodeName = nodeNameField.getText().trim();
                removeNode(nodeName);
            }
        });

        addRootButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRootNode();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTree();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTree();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Node Name:"));
        inputPanel.add(nodeNameField);
        inputPanel.add(new JLabel("Parent Node Name:"));
        inputPanel.add(parentNodeField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRootButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
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
            nodesMap.put(nodeName, newNode);
        } else {
            TreeNode parentNode = nodesMap.get(parentNodeName);
            if (parentNode == null) {
                JOptionPane.showMessageDialog(this, "Parent node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parentNode.addChild(newNode);
            nodesMap.put(nodeName, newNode);
        }
        repaint();
    }

    public void removeNode(String nodeName) {
        TreeNode nodeToRemove = nodesMap.get(nodeName);
        if (nodeToRemove == null) {
            JOptionPane.showMessageDialog(this, "Node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TreeNode parentNode = findParentNode(nodeToRemove);
        if (parentNode != null) {
            parentNode.removeChild(nodeToRemove);
        }
        nodesMap.remove(nodeName);
        repaint();
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
                repaint();
                JOptionPane.showMessageDialog(this, "Tree loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading tree: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!nodesMap.isEmpty()) {
            int rootX = getWidth() / 2;
            int rootY = 50;
            int levelHeight = 80;
            int siblingWidth = 100;
            int nodeRadius = 20;

            drawTree(g, nodesMap.values().iterator().next(), rootX, rootY, levelHeight, siblingWidth, nodeRadius);
        }
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int levelHeight, int siblingWidth, int nodeRadius) {
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.drawString(node.name, x - 5, y + 5);

        java.util.List<TreeNode> children = node.children;
        int numChildren = children.size();
        int totalWidth = (numChildren - 1) * siblingWidth;
        int startX = x - totalWidth / 2;

        for (int i = 0; i < numChildren; i++) {
            TreeNode child = children.get(i);
            int childX = startX + i * siblingWidth;
            int childY = y + levelHeight;
            g.drawLine(x, y + nodeRadius, childX, childY - nodeRadius);
            drawTree(g, child, childX, childY, levelHeight, siblingWidth, nodeRadius);
        }
    }

    private static class TreeNode implements Serializable {
        String name;
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
