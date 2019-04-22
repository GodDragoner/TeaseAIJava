package me.goddragon.teaseai.utils.media;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Created by GodDragon on 15.08.2018.
 */
public class Animation extends Transition {

    private ImageView imageView;
    private int count;

    private int lastIndex;

    private Image[] sequence;

    protected Animation() {
    }

    public Animation(Image[] sequence, double durationMs) {
        init(sequence, durationMs);
    }

    protected void init(Image[] sequence, double durationMs) {
        this.sequence = sequence;
        this.count = sequence.length;

        setCycleCount(1);
        setCycleDuration(Duration.millis(durationMs));
        setInterpolator(Interpolator.LINEAR);
    }

    public void play(ImageView imageView) {
        this.imageView = imageView;
        imageView.setImage(sequence[0]);
        play();
    }

    protected void interpolate(double k) {
        final int index = Math.min((int) Math.floor(k * count), count - 1);
        if (index != lastIndex) {
            imageView.setImage(sequence[index]);
            lastIndex = index;
        }
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageView getView() {
        return imageView;
    }
}