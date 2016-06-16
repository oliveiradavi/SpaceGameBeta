package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Laser extends GameObject {

    private int velocity = 15;
    private Animation animation;
    private Bitmap[] image;

    public Laser(Bitmap sprite, int x, int y) {
        animation = new Animation();
        this.x = x;
        this.y = y;


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
        System.out.println("The Y is: " + y);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
