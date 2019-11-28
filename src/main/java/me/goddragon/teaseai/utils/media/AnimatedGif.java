package me.goddragon.teaseai.utils.media;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

/**
 * Created by GodDragon on 15.08.2018.
 */
public class AnimatedGif extends Animation {

    public AnimatedGif(String filename) {
        GifDecoder d = new GifDecoder();
        d.read(filename);

        double durationMs = 0;

        Image[] sequence = new Image[d.getFrameCount()];
        for (int i = 0; i < d.getFrameCount(); i++) {
            WritableImage wimg = null;
            BufferedImage bimg = d.getFrame(i);
            sequence[i] = SwingFXUtils.toFXImage(bimg, wimg);
            /*ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {
                ImageIO.write(bimg, "gif", os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream is = new ByteArrayInputStream(os.toByteArray());
            sequence[i] = new Image(is);*/

            durationMs += d.getDelay(i);
        }

        super.init(sequence, durationMs);
    }
}
