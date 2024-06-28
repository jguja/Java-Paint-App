import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Shape
{
    private int x1, y1, x2, y2; // coordinates of the points defining the shape
    private Color color; // color of the shape
    private BasicStroke stroke; // drawing style of the shape (line thickness)
    private int type; // shape type, 0=Free draw, 1=Line, 2=Rectangle, 3=Circle, 4=Triangle

    public Shape(int x1, int y1, int x2, int y2, Color color, BasicStroke stroke, int type)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.stroke = stroke;
        this.type = type;
    }

    public void draw(Graphics2D g2d)
    {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        switch (type) {
            case 1: // Line
                g2d.drawLine(x1, y1, x2, y2);
                break;
            case 2: // Rectangle
                g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case 3: // Circle
                g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case 4: // Triangle
                int[] xPoints = {x1, (x1 + x2) / 2, x2};
                int[] yPoints = {y2, y1, y2};
                g2d.drawPolygon(xPoints, yPoints, 3);
                break;
        }
    }
    // this method determines if a given point is inside a given shape
    // mainly for the eraser functionality, to determine if a given shape
    // is under the mouse cursor when the user wants to erase it
    public boolean contains(int x, int y)
    {
        switch (type) {
            case 1: // Line
                return new java.awt.geom.Line2D.Float(x1, y1, x2, y2).ptSegDist(x, y) <= stroke.getLineWidth() / 2;
            case 2: // Rectangle
                return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)).contains(x, y);
            case 3: // Circle
                return new Ellipse2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)).contains(x, y);
            case 4: // Triangle
                int[] xPoints = {x1, (x1 + x2) / 2, x2};
                int[] yPoints = {y2, y1, y2};
                return new java.awt.Polygon(xPoints, yPoints, 3).contains(x, y);
            default:
                return false;
        }
    }
}
