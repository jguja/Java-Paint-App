import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
//All tool operations are synchronized with PaintCanvas
public class PaintToolbar extends JToolBar
{
    private JTextField brushSizeField;
    private JButton eraserBtn;
    private JButton undoBtn; // undo
    private JButton redoBtn; // redo
    private JButton clearBtn;
    private Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE};
    private PaintCanvas canvas; // PaintCanvas is managed by the toolbar

    public PaintToolbar(PaintCanvas canvas) // constructor takes PaintCanvas as an argument,
    // to be able to update the tools according to user actions on the canvas
    {
        this.canvas = canvas;

        brushSizeField = new JTextField("20", 3);
        brushSizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateBrushSize();
            }
        });
        // when the user releases a key after entering text,
        // the keyReleased(KeyEvent e) method will be called, updateBrushSize() will be triggered
        brushSizeField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                updateBrushSize();
            }
        });

        add(brushSizeField);

        ImageIcon brushIcon = new ImageIcon(ImageUtils.loadImage("icons/brush.png"));
        JButton brushBtn = new JButton(brushIcon);
        brushBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.setActiveTool(0); // active tool brush
                canvas.setSelectedColor(Color.BLACK);
                eraserBtn.setBorderPainted(false); // disables border effect on the eraser button, eraser is not active
            }
        });
        add(brushBtn);

        ImageIcon eraserIcon = new ImageIcon(ImageUtils.loadImage("icons/eraser.png"));
        eraserBtn = new JButton(eraserIcon);
        eraserBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setActiveTool(5); // 5 is the eraser id, active tool eraser
                eraserBtn.setBorderPainted(true); // changes the appearance of the eraser button by setting the border
            }
        });
        add(eraserBtn);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        for (Color color : colors) { // iterate over the array of colors
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(20, 20));
            colorBtn.setBackground(color);
            colorBtn.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    canvas.setSelectedColor(color); // sets the selected color on the canvas
                }
            });
            colorPanel.add(colorBtn);
        }

        add(colorPanel);

        undoBtn = new JButton("Undo");
        undoBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.undo(); // undo the last operation on the canvas
            }
        });

        add(undoBtn);

        redoBtn = new JButton("Redo");
        redoBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.redo(); // redo the undone operations on the canvas
            }
        });

        add(redoBtn);

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.clearCanvas();
            }
        });
        add(clearBtn);

        JButton lineBtn = new JButton(new ImageIcon(ImageUtils.loadImage("icons/line.png")));
        lineBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.setActiveTool(1);
            }
        });
        add(lineBtn);

        JButton rectangleBtn = new JButton(new ImageIcon(ImageUtils.loadImage("icons/rectangle.png")));
        rectangleBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.setActiveTool(2);
            }
        });
        add(rectangleBtn);

        JButton circleBtn = new JButton(new ImageIcon(ImageUtils.loadImage("icons/circle.png")));
        circleBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.setActiveTool(3);
            }
        });
        add(circleBtn);

        JButton triangleBtn = new JButton(new ImageIcon(ImageUtils.loadImage("icons/triangle.png")));
        triangleBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                canvas.setActiveTool(4);
            }
        });
        add(triangleBtn);
    }

    private void updateBrushSize()
    {
        try {
            // get the text from the text field and convert it to int
            int size = Integer.parseInt(brushSizeField.getText());
            canvas.setBrushSize(size); // set the new brush size on the canvas
            canvas.repaint(); // refresh the canvas to make the changes visible
        } catch (NumberFormatException ex) {
            // if the text cannot be converted to Int, display an error message
            JOptionPane.showMessageDialog(null, "Invalid brush size");
        }
    }
}
