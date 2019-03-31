package me.goddragon.teaseai.utils.media;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.imagescaling.ResampleFilters;
import me.goddragon.teaseai.utils.libraries.imagescaling.ResampleOp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

public class ImageUtils {

    public static void setImageInView(File image, ImageView imageView) {
        if(image == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Can't set image to null (should be handled by MediaHandler)");
        }

        double paneWidth = ((Region) imageView.getParent()).getWidth();
        double paneHeight = ((Region) imageView.getParent()).getHeight();

        BufferedImage bufferedImage = ImageUtils.resizeImage(image, paneWidth, paneHeight);

        if(bufferedImage == null) {
            //Fall back if we were unable to properly resize it
            imageView.setImage(new Image(image.toURI().toString()));
        } else {
            imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        }
    }

    public static BufferedImage resizeImage(File image, double paneWidth, double paneHeight) {
        BufferedImage scaledImage = null;

        try {
            BufferedImage originalImage = ImageIO.read(image);

            if(originalImage == null) {
                return null;
            }

            int originalImageHeight = originalImage.getHeight();
            int originalImageWidth = originalImage.getWidth();
            double scaleFactorWidth = originalImageWidth / paneWidth;
            double scaleFactorHeight = originalImageHeight / paneHeight;
            boolean needsScaling = true;
            int newWidth = 0;
            int newHeight = 0;

            if (scaleFactorHeight > scaleFactorWidth) {
                if (scaleFactorHeight <= 2.0) {
                    needsScaling = false;
                } else {
                    newWidth = (int) (originalImageWidth / scaleFactorHeight);
                    newHeight = (int) (originalImageHeight / scaleFactorHeight);
                }
            } else {
                if (scaleFactorWidth <= 2.0) {
                    needsScaling = false;
                } else {
                    newWidth = (int) (originalImageWidth / scaleFactorWidth);
                    newHeight = (int) (originalImageHeight / scaleFactorWidth);
                }
            }
            if (needsScaling) {
                ResampleOp resizeOp = new ResampleOp(newWidth, newHeight);
                resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
                scaledImage = resizeOp.filter(originalImage, null);
            } else {
                scaledImage = originalImage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scaledImage;
    }

}
