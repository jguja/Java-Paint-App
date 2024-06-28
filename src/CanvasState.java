import java.awt.image.BufferedImage;
import java.util.List;

public class CanvasState
{
    private BufferedImage canvasImage; // this field stores the canvas image
    private List<Shape> shapes; // this field stores the list of shapes on the canvas

    public CanvasState(BufferedImage canvasImage, List<Shape> shapes)
    {
        this.canvasImage = canvasImage;
        this.shapes = shapes;
    }
    // this method provides the canvas image, returning a BufferedImage object
    public BufferedImage getCanvasImage()
    {
        return canvasImage;
    }
    // this method provides the list of shapes, returning a list of Shape objects
    public List<Shape> getShapes()
    {
        return shapes;
    }
}
