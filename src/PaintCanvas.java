import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PaintCanvas extends JPanel
{
    private BufferedImage canvasImage; // stores the drawing on the canvas
    private Color selectedColor = Color.BLACK; // brush color
    private int brushSize = 20; // brush size
    private Stack<CanvasState> undoStack = new Stack<>(); // Stack storing the canvas state for undo functionality
    private Stack<CanvasState> redoStack = new Stack<>(); // Stack storing the canvas state for redo functionality
    private List<Shape> shapes = new ArrayList<>(); // list storing drawn shapes
    private int activeTool = 0; // currently active tool, 0=Free draw, 1=Line, 2=Rectangle, 3=Circle, 4=Triangle, 5=Eraser
    private int x1, y1, x2, y2; // coordinates of the starting and ending points of the shape

    public PaintCanvas(int width, int height)
    {
        initializeCanvasImage(width, height);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(width, height)); // sets the preferred size of the canvas

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) // when the user presses the mouse button
            {
                x1 = e.getX(); // save starting coordinates
                y1 = e.getY();
                saveStateToUndoStack(); // save canvas state to the undo stack
                redoStack.clear();
            }

            @Override
            public void mouseReleased(MouseEvent e) // when the user releases the mouse button
            {
                x2 = e.getX(); // save ending coordinates
                y2 = e.getY();
                if (activeTool != 5) // if the tool is not an eraser
                {
                    shapes.add(new Shape(x1, y1, x2, y2, selectedColor, new BasicStroke(brushSize), activeTool));
                    drawShapeOnCanvas(shapes.get(shapes.size() - 1)); // create a new shape based on the starting
                    // and ending coordinates, selected color, brush thickness, and draw it on the canvas
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e) // when the user drags the mouse, draw the shape in real-time
            {
                if (activeTool == 0 || activeTool == 5) // if the active tool is free draw or eraser
                {
                    Graphics2D g = canvasImage.createGraphics(); // create a graphics context to draw on the image
                    g.setColor(activeTool == 5 ? Color.WHITE : selectedColor);
                    // draw a filled oval on the canvasImage at the cursor's location
                    g.fillOval(e.getX() - brushSize / 2, e.getY() - brushSize / 2, brushSize, brushSize);
                    g.dispose(); // release system resources associated with the Graphics2D object
                    if (activeTool == 5)
                    {
                        eraseShapesAt(e.getX(), e.getY());
                    }
                    repaint(); // refresh the screen to show changes
                }
            }
        });
    }

    @Override
    // draws the background image and all shapes on the panel
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);
        Graphics2D g2d = (Graphics2D) g; // convert Graphics object to Graphics2D object
        for (Shape shape : shapes) // iterate through the shapes collection
        {
            shape.draw(g2d); // draw the shape on the panel
        }
    }

    private void eraseShapesAt(int x, int y)
    {
        List<Shape> shapesToRemove = new ArrayList<>(); // list for removing shapes
        for (Shape shape : shapes) {
            if (shape.contains(x, y))
            {
                shapesToRemove.add(shape);
            }
        }
        shapes.removeAll(shapesToRemove); // all shapes contained in the shapesToRemove list
        // will be removed from the main shapes list
    }

    private void initializeCanvasImage(int width, int height)
    {
        canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvasImage.createGraphics(); // we can draw on the image using methods available in Graphics2D
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight()); // draw a rectangle
        // with dimensions corresponding to the entire image area and fill it white
        g2d.dispose(); // release the graphics context resources
    }

    public void saveImage()
    {
        JFileChooser fileChooser = new JFileChooser(); // save file dialog
        fileChooser.setDialogTitle("Save Image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images (*.png)", "png");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("image.png"));

        int userSelection = fileChooser.showSaveDialog(this); // display the save file dialog

        if (userSelection == JFileChooser.APPROVE_OPTION) // if the user approved the file to save
        {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) // if the file name does not end with .png
            {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                ImageIO.write(canvasImage, "PNG", fileToSave); // the image is saved to the selected file
                JOptionPane.showMessageDialog(this, "Image saved successfully to " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage());
            }
        }
    }

    public void openImage()
    {
        JFileChooser fileChooser = new JFileChooser(); // open file dialog
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null); // display the open file dialog and wait for file selection
        if (returnValue == JFileChooser.APPROVE_OPTION) // if the user approved the selection
        {
            File selectedFile = fileChooser.getSelectedFile(); // get the selected file
            try {
                BufferedImage image = ImageIO.read(selectedFile);
                canvasImage = ImageUtils.deepCopy(image); // clone the image and set it as canvasImage
                setPreferredSize(new Dimension(canvasImage.getWidth(), canvasImage.getHeight()));
                // set the canvas size to the image size
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // the method saves the current canvas state to the undo stack
    private void saveStateToUndoStack()
    {
        // CanvasState object is added to the undo stack
        undoStack.push(new CanvasState(ImageUtils.deepCopy(canvasImage), new ArrayList<>(shapes)));
    }

    // undo operation
    public void undo()
    {
        if (!undoStack.isEmpty())
        {
            // CanvasState object will be saved to the redo stack
            redoStack.push(new CanvasState(ImageUtils.deepCopy(canvasImage), new ArrayList<>(shapes)));
            CanvasState state = undoStack.pop(); // get the last state from the undo stack
            canvasImage = state.getCanvasImage();
            shapes = state.getShapes();
            repaint(); // display the new canvas state
        }
    }
    // redo operation
    public void redo()
    {
        if (!redoStack.isEmpty())
        {
            // save the current state before performing redo
            // so it is possible to undo the redo operation later
            undoStack.push(new CanvasState(ImageUtils.deepCopy(canvasImage), new ArrayList<>(shapes)));
            CanvasState state = redoStack.pop(); // get the state from the top of the redo stack
            canvasImage = state.getCanvasImage(); // restore the canvas state and shapes
            shapes = state.getShapes();
            repaint();
        }
    }

    // methods to set field values in the class instance
    public void setSelectedColor(Color color)
    {
        selectedColor = color;
    }

    public void setBrushSize(int size)
    {
        brushSize = size;
    }

    public void setActiveTool(int tool)
    {
        activeTool = tool;
    }

    public void clearCanvas()
    {
        shapes.clear();
        initializeCanvasImage(getWidth(), getHeight()); // initialize a new canvas image, setting width and height to current
        repaint();
    }

    private void drawShapeOnCanvas(Shape shape)
    {
        Graphics2D g2d = canvasImage.createGraphics(); // Graphics2D object that allows drawing on the canvas image
        shape.draw(g2d);
        g2d.dispose(); // release resources
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        resizeCanvasImage(width, height); // adjust the canvas image size to the new component dimensions
    }

    @Override
    // alternative way of setting the component size
    public void setSize(Dimension d)
    {
        super.setSize(d);
        resizeCanvasImage(d.width, d.height);
    }

    private void resizeCanvasImage(int width, int height)
    {
        // if the new dimensions are larger than the current canvas dimensions
        if (canvasImage.getWidth() < width || canvasImage.getHeight() < height)
        {
            // create a new image
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            g2d.drawImage(canvasImage, 0, 0, null); // copy the contents of the current canvas image to the new image
            g2d.dispose();
            canvasImage = newImage; // the canvas image reference is replaced with the new image reference
        }
    }
}
