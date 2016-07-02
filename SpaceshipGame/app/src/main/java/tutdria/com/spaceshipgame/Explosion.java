package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Explosion extends GameObject {
    private Animation animation = new Animation();
    private Bitmap[] image;

    public Explosion (Bitmap spriteheet, int x, int y) {

        image = new Bitmap[7];

        width = spriteheet.getWidth()/7;
        height = spriteheet.getHeight();

        this.x = x;
        this.y = y;

        for(int i=0;i<image.length;i++) {
            image[i] = Bitmap.createBitmap(spriteheet, i*width, 0 , width, height);
        }

        animation.setFrames(image);
        animation.setDelay(50);
    }

    public void update() {
        animation.update();
    }

    public boolean getPlayed() {
        if (animation.playedOnce()) {
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas)
    {
        if(!animation.playedOnce()){
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }
}
