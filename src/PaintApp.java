import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PaintApp extends JFrame
{
    private PaintCanvas canvas;
    private PaintToolbar toolBar;

    public PaintApp()
    {
        setTitle("PaintApp");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        canvas = new PaintCanvas(600, 600);
        toolBar = new PaintToolbar(canvas);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.openImage();
            }
        });

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.saveImage();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() {
                new PaintApp();
            }
        });
    }
}
