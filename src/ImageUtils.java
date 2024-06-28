import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Hashtable;

// this class is used in the PaintCanvas class
// for loading images from the disk and creating deep copies of images.
public class ImageUtils
{
    // this method creates a deep copy of the BufferedImage object
    public static BufferedImage deepCopy(BufferedImage bi)
    {
        ColorModel cm = bi.getColorModel(); // get the ColorModel from the original image
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied(); // check if the image has an alpha channel
        WritableRaster raster = bi.copyData(null); // create a new writable raster based on the original image pixels
        return new BufferedImage(cm, raster, isAlphaPremultiplied, new Hashtable<>());
    }

    // this method is used to load an image from a file on the disk
    public static Image loadImage(String path) // returns an Image object, which is the loaded image
    {
        try {
            return ImageIO.read(new File(path)); // use the ImageIO class to load the image from the file
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
