package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Laser extends GameObject {

    private Random rand = new Random();
    int r;
    private int velocity = 25;
    boolean active = false;
    private Animation animation;
    private Bitmap[] image;

    public Laser(Bitmap sprite, int x, int y) {

        this.x = x;
        this.y = y;

        animation = new Animation();
        image = new Bitmap[1];
        image[0] = Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());

        width = image[0].getWidth();
        height = image[0].getHeight();

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update() {
        x+= velocity;
        animation.update();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
