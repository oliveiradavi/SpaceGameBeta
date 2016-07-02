package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class EnemyLaser extends GameObject {

    private Bitmap image;
    private int velocity = 15;

    public EnemyLaser(Bitmap sprite, int x, int y) {

        image = sprite;
        this.x = x;
        this.y = y;

        width = image.getWidth();
        height = image.getHeight();
    }

    public void update() {
        x-=velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }

}
