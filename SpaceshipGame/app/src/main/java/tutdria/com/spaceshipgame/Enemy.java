package tutdria.com.spaceshipgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Enemy extends GameObject {
    Bitmap image;
    private int velocity = 12;
    private boolean item = false;

    public Enemy(Bitmap sprite, int y) {
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;
    }

    public Enemy(Bitmap sprite, int y, boolean b) {
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        x = GamePanel.bgWidth;
        this.y = y;

        item = b;
    }

    public Enemy(Bitmap sprite, int y, int x) {
        image = sprite;

        width = sprite.getWidth();
        height = sprite.getHeight();

        this.x = x;
        this.y = y;
    }

    public boolean getItem() {
        return item;
    }

    public void update() {
        x-= velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
