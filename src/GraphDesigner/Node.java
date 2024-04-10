package GraphDesigner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private Point point;
    private String nodeName;
    private List<Edge> edges;
    private final Color color = Color.BLUE;
    private final int size = 35;

    public Node(Point point, String nodeName) {
        this.point = point;
        this.nodeName = nodeName;
        this.edges = new ArrayList<>(); 
        
    }

    public Point getPoint() {
        return point;
    }

    public String getNodeName() {
        return nodeName;
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
    
    public void setEdges(List<Edge> edges) {
    	this.edges = edges;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
    
    @Override
    public String toString() {
    	return nodeName +"";
    }
    

    public boolean contains(Point p) {
        int tolerance = 10; // Adjust tolerance as needed
        return (Math.abs(point.x - p.x) <= tolerance && Math.abs(point.y - p.y) <= tolerance);
    }
    
    public void moveBy(int dx, int dy) {
        point.translate(dx, dy);
    }

    public void draw(Graphics g) {
        int nodeSize = size;
        // Change font size and style for node name
        Font font = new Font("Arial", Font.BOLD, 13);
        g.setFont(font);
        
        // Set the thickness of the border
        Graphics2D g2d = (Graphics2D) g;
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2)); // Adjust the thickness as needed

        // Draw node border
        g.setColor(Color.BLACK);
        g.drawOval(point.x - nodeSize / 2, point.y - nodeSize / 2, nodeSize, nodeSize);

        // Restore the original stroke
        g2d.setStroke(originalStroke);
        
        g.setColor(color);
        g.fillOval(point.x - nodeSize / 2, point.y - nodeSize / 2, nodeSize, nodeSize);
        if (nodeName != null) {
            g.setColor(Color.WHITE);
            g.drawString(nodeName, point.x - nodeName.length() * 3, point.y + 5);
        }
    }
}