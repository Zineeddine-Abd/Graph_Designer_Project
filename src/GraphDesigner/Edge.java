package GraphDesigner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;

public class Edge {
	
    private Node start;
    private Node end;
    private int weight;
    private final Color color = Color.BLACK;
    private final int thickness = 4;

    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
        this.weight = 1; // Default weight == 1
    }

    public Edge(Node start, Node end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }
    
    public Node getStart() {
    	return start;
    }
    
    public Node getEnd() {
    	return end;
    }
    
    public int getWeight() {
    	return weight;
    }
    
    public boolean isPointNearLine(Point point) {
        // Calculate the distance from the point to the line segment defined by the edge
        double distance = Line2D.ptSegDist(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y, point.x, point.y);
        
        // Define a threshold distance within which we consider the point to be "near" the line
        double threshold = 5.0; // Adjust this threshold as needed
        
        // Return true if the distance is within the threshold
        return distance <= threshold;
    }
    
    public void draw(Graphics g, boolean weighted, boolean directed) {
        
    	g.setColor(color);
        
    	//----------------------------------------
        Graphics2D g2d = (Graphics2D) g;

        // Save the original stroke
        Stroke originalStroke = g2d.getStroke();

        // Set the custom stroke thickness
        g2d.setStroke(new BasicStroke(thickness));

        // Draw the edge
        g2d.setColor(color);
        g2d.drawLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        
        // Restore the original stroke
        g2d.setStroke(originalStroke);
        //----------------------------------------
        
        g.drawLine(start.getPoint().x, start.getPoint().y, end.getPoint().x, end.getPoint().y);
        if (weighted) {
        	// Draw weight in a square at the midpoint of the edge
            int midX = (start.getPoint().x + end.getPoint().x) / 2;
            int midY = (start.getPoint().y + end.getPoint().y) / 2;
            int squareSize = 20; // Size of the square for displaying weight
            int x = midX - squareSize / 2;
            int y = midY - squareSize / 2;
            
            g.setColor(Color.YELLOW);
            g.drawRect(x, y, squareSize, squareSize);
            g.fillRect(x, y, squareSize, squareSize);
            
            // Draw weight inside the square
            g.setColor(Color.BLACK);
            Font font = new Font("Arial", Font.BOLD, 12); // Adjust font size and style as needed
            g.setFont(font);
            g.drawString(String.valueOf(weight), midX - 5, midY + 5); // Adjust position of the weight text
            
        }
        if (directed) {
            drawArrow(g2d, start.getPoint(), end.getPoint());
        }
    }

    public void drawArrow(Graphics g, Point start, Point end) {
        Graphics2D g2d = (Graphics2D) g;

        // Calculate the angle of the edge
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        // Calculate the distance from the end point to the border of the node
        int arrowSize = Math.max(10, thickness * 2); // Adjust the arrow size based on edge thickness

        // Calculate the intersection point between the edge line and the border of the node
        double nodeRadius = Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
        int intersectionX = end.x - (int) (arrowSize * (end.x - start.x) / nodeRadius);
        int intersectionY = end.y - (int) (arrowSize * (end.y - start.y) / nodeRadius);

        // Calculate the length of the arrow (increase by 10 pixels)
        int arrowLength = arrowSize + 10;

        // Calculate the arrow endpoints
        int x1 = intersectionX - (int) (arrowLength * Math.cos(angle - Math.PI / 6));
        int y1 = intersectionY - (int) (arrowLength * Math.sin(angle - Math.PI / 6));
        int x2 = intersectionX - (int) (arrowLength * Math.cos(angle + Math.PI / 6));
        int y2 = intersectionY - (int) (arrowLength * Math.sin(angle + Math.PI / 6));

        // Save the original stroke
        Stroke originalStroke = g2d.getStroke();

        // Set the custom stroke thickness
        g2d.setStroke(new BasicStroke(thickness));

        // Draw the arrow lines
        g2d.drawLine(intersectionX, intersectionY, x1, y1);
        g2d.drawLine(intersectionX, intersectionY, x2, y2);

        // Restore the original stroke
        g2d.setStroke(originalStroke);
    }


}