package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Laser extends GameObject {

    private int velocity = 35;
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
