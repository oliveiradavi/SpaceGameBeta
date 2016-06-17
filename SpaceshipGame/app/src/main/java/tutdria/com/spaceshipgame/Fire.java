package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Fire extends GameObject {
    private Bitmap[] image;
    private Animation animation = new Animation();
    private int imageWidth;

    public Fire(Bitmap sprite) {

        imageWidth = sprite.getWidth()/3;
        x = 160 - imageWidth;
        y =-250;
        image = new Bitmap[3];
        width = sprite.getWidth() / 3;

        for(int i=0;i<image.length;i++) {
            image[i] = Bitmap.createBitmap(sprite, i*imageWidth, 0, width, sprite.getHeight());
        }

        animation.setFrames(image);
        animation.setDelay(250);
    }

    public void update(int x, int y) {
        this.x = x;
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
