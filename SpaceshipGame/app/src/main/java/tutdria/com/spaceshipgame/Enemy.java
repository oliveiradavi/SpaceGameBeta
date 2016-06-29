package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends GameObject {
    Bitmap image;
    private int velocity = 10;

    public Enemy(Bitmap sprite, int y) {
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;
    }

    public void update() {
        x-= velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
