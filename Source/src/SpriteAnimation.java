import javafx.scene.image.Image;

/**
 * This class handles the sprite animation of the player.
 * Code originally from https://github.com/foreignguymike/legacyYTtutorials/tree/master/dragontale, but modified by Radu Bucurescu.
 *
 * @Author Radu
 */
public class SpriteAnimation {
    private Image[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;

    public SpriteAnimation() {
        playedOnce = false;
    }

    public void setFrames(Image[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setFrame(int frame) {
        currentFrame = frame;
    }

    public void update() {
        if (delay == -1) {
            return;
        }

        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if (elapsed > delay) {
            currentFrame++;
            startTime = System.nanoTime();
        }
        if (currentFrame == frames.length) {
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public int getFrame() {
        return currentFrame;
    }

    public Image getImage() {
        return frames[currentFrame];
    }

    public boolean hasPlayedOnce() {
        return playedOnce;
    }
}
