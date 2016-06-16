package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fire extends GameObject {
    private Bitmap[] image;
    private Animation animation = new Animation();

    public Fire(Bitmap sprite) {
        x = 100 - sprite.getWidth();
        y =-250;
        image = new Bitmap[1];
        image[0] = Bitmap.createBitmap(sprite, 0, 0, sprite.getWidth(), sprite.getHeight());
        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update(int y) {
        this.y = y - image[0].getHeight()/2;
        animation.update();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public void reset() {
        y = -250;
    }
}
