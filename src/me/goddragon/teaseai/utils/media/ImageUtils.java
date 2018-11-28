package me.goddragon.teaseai.utils.media;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import me.goddragon.teaseai.utils.libraries.imagescaling.ResampleFilters;
import me.goddragon.teaseai.utils.libraries.imagescaling.ResampleOp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtils {

    public static void setImageInView(File image, ImageView imageView) {
        double paneWidth = ((StackPane) imageView.getParent()).getWidth();
        double paneHeight = ((StackPane) imageView.getParent()).getHeight();

        imageView.setImage(SwingFXUtils.toFXImage(ImageUtils.resizeImage(image, paneWidth, paneHeight), null));
    }

    public static BufferedImage resizeImage(File image, double paneWidth, double paneHeight) {
        BufferedImage scaledImage = null;
        try {
            BufferedImage originalImage = ImageIO.read(image);
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
